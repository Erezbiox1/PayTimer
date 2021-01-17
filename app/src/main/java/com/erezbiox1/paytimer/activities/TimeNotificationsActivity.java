/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.erezbiox1.paytimer.R;
import com.erezbiox1.paytimer.utils.TimeNotificationsController;

public class TimeNotificationsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use the general settings_activity layout, a shared layout that is used to inflate settings like activities
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        // setup the actionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // register this activity as a listener to preference changes
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Called when the preferences change
     * @param sharedPreferences the changed preferences
     * @param key the changed key
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // If the key that was changed starts with time_notifications ( all time-notification related preferences )
        // then reschedule the notification with the updated settings.
        if(key.toLowerCase().startsWith("time_notifications"))
            TimeNotificationsController.rescheduleNotification(this);
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

    // The settings fragment class that will show and handle the settings from time_preferences.xml
    public static class SettingsFragment extends PreferenceFragmentCompat {
        /**
         * inflates the preferences
         * @param savedInstanceState previous saved instance data
         * @param rootKey the root key of the preferences
         */
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.time_preferences, rootKey);
        }
    }
}