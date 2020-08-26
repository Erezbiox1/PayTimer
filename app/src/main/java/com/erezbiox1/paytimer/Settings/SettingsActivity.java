/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;

import com.erezbiox1.paytimer.Reminders.GeofenceReceiver;
import com.erezbiox1.paytimer.Reminders.LocationHandler;
import com.erezbiox1.paytimer.R;
import com.erezbiox1.paytimer.Reminders.ReminderController;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements ButtonPreference.ButtonPrefCallback {

    private LocationHandler locationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        locationHandler = new LocationHandler(this, getPrefs());
    }

    @Override // TODO: ADD FUNCTIONALITY
    public void onClick(ButtonPreference button) {
        String message = "";

        switch (button.getKey().toLowerCase()) {
            case "pick_location":
                pickLocation();
                break;
            case "pick_time":
                message = "Please pick a time...";
                ReminderController.notify(this);
                break;
            case "import":
                message = "Importing all data...";
                break;
            case "export":
                message = "Exporting all data...";
                break;
            case "delete":
                message = "Deleting all data...";
                break;
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void pickLocation() {
        locationHandler.setGeofence();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LocationHandler.LOCATION_PERM_RESULT:
                locationHandler.setGeofence();
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private SharedPreferences getPrefs(){
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SeekBarPreference seekbar = findPreference("stop_after_time");
            updateSeekbarValue(seekbar, seekbar.getValue());
            Objects.requireNonNull(seekbar).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updateSeekbarValue((SeekBarPreference) preference, Integer.parseInt(String.valueOf(newValue)));
                    return true;
                }
            });
        }

        private void updateSeekbarValue(SeekBarPreference preference, int value){
            preference.setTitle(getString(R.string.stop_after_time_title, value));
        }
    }
}