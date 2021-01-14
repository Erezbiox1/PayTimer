/*
 * Copyright (c) 2021. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.activities;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.erezbiox1.paytimer.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements OnFailureListener, OnSuccessListener<AuthResult> {

    // TODO: Add a viewModel

    private FirebaseAuth firebaseAuth;
    private Button loginBtn, registerBtn, logoutBtn;
    private TextInputLayout emailField, passwordField;

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

    @Override
    public void onSuccess(AuthResult authResult) {
        Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
        updateUi(authResult.getUser());
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Log.i("LoginActivity", "Failed logging in: " + e.getMessage());

        String message = e.getLocalizedMessage();
        
        if(e.getMessage().contains("no user record"))
            message = getString(R.string.login_failed_user_doesnt_exists);
        if(e.getMessage().contains("already in use"))
            message = getString(R.string.login_failed_user_already_exists);
        if(e.getMessage().contains("at least 6 characters"))
            message = getString(R.string.login_failed_password_short);
        if(e.getMessage().contains("password is invalid"))
            message = getString(R.string.login_failed_password_incorrect);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


}