/*
 * Copyright (c) 2021. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.erezbiox1.paytimer.database.ShiftRepository;
import com.erezbiox1.paytimer.model.Shift;
import com.erezbiox1.paytimer.utils.DownloadImageTask;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.erezbiox1.paytimer.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class LoginActivity extends AppCompatActivity implements OnFailureListener, OnSuccessListener<AuthResult> {

    // TODO: Add a viewModel

    private static final int FILE_PICKER = 420;

    private FirebaseAuth firebaseAuth;
    private Button loginBtn, registerBtn, logoutBtn;
    private TextInputLayout emailField, passwordField;
    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Load firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Load views
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);

        loginBtn = findViewById(R.id.login_button);
        registerBtn = findViewById(R.id.register_button);
        logoutBtn = findViewById(R.id.logout_button);

        profilePic = findViewById(R.id.profile_pic);
    }

    private void updateUi(FirebaseUser user){
        boolean isLoggedIn = user != null;

        loginBtn.setEnabled(!isLoggedIn);
        registerBtn.setEnabled(!isLoggedIn);
        logoutBtn.setEnabled(isLoggedIn);

        emailField.setEnabled(!isLoggedIn);
        passwordField.setEnabled(!isLoggedIn);

        emailField.getEditText().setText(isLoggedIn ? user.getEmail() : "");
        passwordField.getEditText().setText(isLoggedIn ? "*********" : "");

        if(!isLoggedIn)
            profilePic.setImageResource(R.drawable.ic_profile);
        else
            new DownloadImageTask(profilePic).execute(user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : String.format("https://robohash.org/%s.png?set=set4", user.getEmail()));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Pass the user (nullable) to updateUi() so we can adjust the activity behaviour
        // ( what buttons to disable etc. )
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUi(currentUser);
    }

    public void onLogin(View view) {
        // Get the email and password from the fields
        String email = emailField.getEditText().getText().toString(), password = passwordField.getEditText().getText().toString();

        if(email.isEmpty())
            emailField.setError(getString(R.string.login_failed_email_empty));

        if(password.isEmpty())
            passwordField.setError(getString(R.string.login_failed_password_empty));

        if(email.isEmpty() || password.isEmpty())
            return;

        // Login.
        firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(this)
                .addOnFailureListener(this);
    }

    public void onRegister(View view){
        // Get the email and password from the fields
        String email = emailField.getEditText().getText().toString(), password = passwordField.getEditText().getText().toString();

        // Login.
        firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(this)
                .addOnFailureListener(this);
    }

    public void onLogout(View view){
        // Logout.
        firebaseAuth.signOut();
        updateUi(null);
    }

    public void onProfilePicUpload(View view) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) return;

        Intent pickFileIntent = new Intent();
        pickFileIntent.setType("image/*");
        pickFileIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pickFileIntent, getString(R.string.select_profile_pictrue)), FILE_PICKER);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        switch (requestCode){
            case FILE_PICKER:
                if(data != null && data.getData() != null){
                    Uri uri = data.getData();
                    StorageReference storage = FirebaseStorage.getInstance().getReference();

                    StorageReference profilePicRef = storage.child("profiles").child(user.getUid());
                    profilePicRef
                            .putFile(uri)
                            .continueWithTask(task -> {
                                if(!task.isSuccessful())
                                    throw task.getException();

                                return profilePicRef.getDownloadUrl();
                            }).continueWithTask(downloadLink -> {
                                return user
                                        .updateProfile(new UserProfileChangeRequest.Builder()
                                                .setPhotoUri(downloadLink.getResult())
                                                .build());
                            }).addOnSuccessListener(uri1 -> {
                                Toast.makeText(LoginActivity.this, R.string.image_uploaded_success, Toast.LENGTH_SHORT).show();
                                updateUi(user);
                            }).addOnFailureListener(e -> {
                                Toast.makeText(LoginActivity.this, R.string.image_upload_fail, Toast.LENGTH_SHORT).show();
                            });
                }

                break;
        }
    }

    @Override
    public void onSuccess(AuthResult authResult) {
        Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
        updateUi(authResult.getUser());
        resetErrors();
        ShiftRepository.resetInstance();
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Log.i("LoginActivity", "Failed logging in: " + e.getMessage());

        // Reset previous errors
        resetErrors();

        if(e.getMessage().contains("no user record"))
            emailField.setError(getString(R.string.login_failed_user_doesnt_exists));
        else if(e.getMessage().contains("already in use"))
            emailField.setError(getString(R.string.login_failed_user_already_exists));
        else if(e.getMessage().contains("at least 6 characters"))
            passwordField.setError(getString(R.string.login_failed_password_short));
        else if(e.getMessage().contains("password is invalid"))
            passwordField.setError(getString(R.string.login_failed_password_incorrect));
        else Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    private void resetErrors(){
        emailField.setError("");
        passwordField.setError("");
    }
}