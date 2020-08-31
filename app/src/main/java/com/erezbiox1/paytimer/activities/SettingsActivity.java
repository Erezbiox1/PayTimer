/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.erezbiox1.paytimer.utils.LocationController;
import com.erezbiox1.paytimer.R;
import com.erezbiox1.paytimer.utils.ReminderController;
import com.erezbiox1.paytimer.views.preferences.ButtonPreference;

import java.util.Objects;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SettingsActivity extends AppCompatActivity implements ButtonPreference.ButtonPrefCallback {

    private LocationController locationController;

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

        locationController = new LocationController(this);
    }

    @Override
    public void onClick(ButtonPreference button) {
        switch (button.getKey().toLowerCase()) {
            case "pick_location":
                pickLocation();
                break;
            case "pick_time":
                pickTime();
                break;
        }
    }

    private void pickLocation() {
        if (LocationController.checkPermission(this))
            ActivityCompat.requestPermissions(
                    this, new String[]{ ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION }, 1);

        locationController.setGeofence();
    }

    private void pickTime(){
        Intent timeNotificationsIntent = new Intent(this, TimeNotificationsActivity.class);
        startActivity(timeNotificationsIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LocationController.LOCATION_PERM_RESULT:
                locationController.setGeofence();
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

            //setupSeekbar();
        }

        private void setupSeekbar(){
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