/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.views.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceViewHolder;

import com.erezbiox1.paytimer.R;

/**
 * A custom preference used by the settings activity. {@link com.erezbiox1.paytimer.activities.SettingsActivity}
 * Stores a number in a persisted string. ( to avoid floating precision errors )
 */
public class NumberPreference extends ButtonPreference implements
        ButtonPreference.ButtonPrefCallback, DialogInterface.OnClickListener {

    // The shown alert dialog and the editText inside of it
    private AlertDialog alertDialog;
    private EditText editText;

    /**
     * A constructor matching the super constructor
     * @param context provided context
     * @param attrs provided attrs
     */
    public NumberPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Called when the preference manager assigns a view holder to this preference.
     * @param holder the assigned view holder
     */
    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        // Call the super method, allowing to configure the button preference
        super.onBindViewHolder(holder);

        // Set the button preference callback to this preference.
        super.setCallback(this);
    }

    /**
     * Setups the dialog
     */
    private void setupDialog(){
        // Alert dialog builder, built with the activity context provided by the super getActivity()
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title, the dialog layout ( single edit text in the center 0 and a positive button ( callback
        // to this preference onClick(dialogInterface, i) ), and the negative button ( no handler, null. )
        builder
                .setTitle(getContext().getString(R.string.hourly_pay))
                .setView(R.layout.pref_number)
                .setPositiveButton(getContext().getString(R.string.confirm), this)
                .setNegativeButton(getContext().getString(R.string.cancel), null);

        // Create the alert dialog
        alertDialog = builder.create();
    }

    @Override
    public void onClick(ButtonPreference button) {
        // If the alert dialog is null ( the button hasn't been pressed yet )
        if(alertDialog == null)
            // Setup the dialog
            setupDialog();

        // Show the dialog
        alertDialog.show();

        // Find the editText in the alert dialog ( only possible after the dialog have been shown )
        editText = alertDialog.findViewById(R.id.settings_hourly_rate);

        // And set it's default text to the saved string, or a default 24 value.
        editText.setText(getPersistedString("24"));
    }

    /**
     * Called when the positive button ( save button )
     * is clicked in the alert dialog
     * @param dialogInterface provided dialog interface
     * @param i what button was clicked
     */
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        // Get the editText of the alertDialog
        EditText editText = alertDialog.findViewById(R.id.settings_hourly_rate);

        // Save it's value.
        persistString(editText.getText().toString());
    }
}
