/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.Settings;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.erezbiox1.paytimer.R;

public class NumberPreference extends ButtonPreference implements
        ButtonPreference.ButtonPrefCallback, DialogInterface.OnClickListener {

    private AlertDialog alertDialog;
    private EditText editText;

    public NumberPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        super.setCallback(this);
    }

    private void setupDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder
                .setTitle("Hourly Rate")
                .setView(R.layout.pref_number)
                .setPositiveButton("Set", this)
                .setNegativeButton("Cancel", null);

        alertDialog = builder.create();
    }

    @Override
    public void onClick(ButtonPreference button) {
        if(alertDialog == null)
            setupDialog();

        alertDialog.show();
        editText = alertDialog.findViewById(R.id.settings_hourly_rate);
        editText.setText(getPersistedString("24"));
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        EditText editText = alertDialog.findViewById(R.id.settings_hourly_rate);
        persistString(editText.getText().toString());
    }
}
