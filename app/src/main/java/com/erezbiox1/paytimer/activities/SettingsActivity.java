/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.erezbiox1.paytimer.R;
import com.erezbiox1.paytimer.utils.LocationController;
import com.erezbiox1.paytimer.views.preferences.ButtonPreference;

import java.util.Objects;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SettingsActivity extends AppCompatActivity implements ButtonPreference.ButtonPrefCallback {

    // Location controller that will be used to set the current location and add the geo-fence.
    private LocationController locationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use the general settings_activity layout, a shared layout that is used to infalte settings like activities
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // create the location controller ( pass it our reference )
        locationController = new LocationController(this);
    }

    /**
     * When one of the custom button preferences will be clicked this button is called
     * @param button the button that was clicked
     */
    @Override
    public void onClick(ButtonPreference button) {
        // handle the buttons according to their specified key bt the root_preferences.xml file.
        switch (button.getKey().toLowerCase()) {
            case "pick_location":
                // if the "Pick Location" button is picked, invoke the pickLocation() method.
                pickLocation();
                break;
            case "pick_time":
                // if the "Pick Time" Button is picked, invoke the pickTime() method.
                pickTime();
                break;
        }
    }

    /**
     * Will prompt the user for permission and then will set the current location as the geofence trigger.
     */
    private void pickLocation() {
        // Check if we have the necessary permissions.
        if (LocationController.checkPermission(this))
            // If not, ask for them.
            ActivityCompat.requestPermissions(
                    this, new String[]{ ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION }, 1);

        // Invoke the setGeofence() method inside the location controller,
        // notice if the user denied the requested permission this method will
        // do nothing.
        locationController.setGeofence();
    }

    /**
     * Will start the TimeNotificationActivity, a time picking settings fragment that will allow the user to change the specified time.
     */
    private void pickTime(){
        Intent timeNotificationsIntent = new Intent(this, TimeNotificationsActivity.class);
        startActivity(timeNotificationsIntent);
    }

    /**
     *
     * @param requestCode permission request code
     * @param permissions permissions requested
     * @param grantResults request result.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Checked what permission was granted
        // TODO: FIX
        switch (requestCode){
            case LocationController.LOCATION_PERM_RESULT:
                // If the location permission was granted, invoke the setGeofence() method.
                locationController.setGeofence();
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * called when an option on the menu selected
     * @param item selected menu item
     * @return was the event handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // If the option is home, finish the activity ( the parent activity activity is MainActivity
        // so finishing this activity will return the user to the home activity. )
        if(item.getItemId() == R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // The settings fragment class that will show and handle the settings from root_preferences.xml
    public static class SettingsFragment extends PreferenceFragmentCompat {

        /**
         * inflates the preferences
         * @param savedInstanceState previous saved instance data
         * @param rootKey the root key of the preferences
         */
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            // Setups the seekbar.
            // TODO: ADD THIS FEATURE
            //setupSeekbar();
        }


        /**
         * Setups the seekbar
         */
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

        /**
         * Called when the seekbar value changes.
         * @param preference change seekbar preference
         * @param value the changed value.
         */
        private void updateSeekbarValue(SeekBarPreference preference, int value){
            // Change the seekbar title to "Will stop after {value} hours."
            preference.setTitle(getString(R.string.stop_after_time_title, value));
        }
    }
}