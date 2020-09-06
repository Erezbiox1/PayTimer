/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.views.preferences;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import com.erezbiox1.paytimer.R;
import com.erezbiox1.paytimer.views.spinners.TimeSpinner;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimePreference extends Preference implements TimeSpinner.OnSpinnerTimeSelect {

    // The time spinner used in the preference, saved hour and minute
    private TimeSpinner timeSpinner;
    private int hour, minute;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Inflate the pref_time layout for the preference widget.
        setWidgetLayoutResource(R.layout.pref_time);

        // Load the saved time to the preference.
        loadTime();
    }

    /**
     * Called when the preference manager assigns a view holder to this preference.
     * @param holder the assigned view holder
     */
    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        // Set the view holder unclickable, find the time spinner
        holder.itemView.setClickable(false);
        timeSpinner = (TimeSpinner) holder.findViewById(R.id.time_pref_spinner);

        // Set the saved hour and minute, and also set this preference as a listener to the time spinner.
        timeSpinner.setHour(hour);
        timeSpinner.setMinute(minute);
        timeSpinner.setOnSpinnerTimeSelect(this);
    }

    /**
     * Load the time saved in the preference ( formatted as a string so we can save both hours and minutes, formatted HH:mm - hours:minutes - 13:30 )
     */
    private void loadTime(){
        // Get the default time ( now, formatted HH:mm )
        String defaultTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(System.currentTimeMillis());

        // Get the saved time if there is one, otherwise the defaultTime we just formatted
        String time = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(getKey(), defaultTime);

        // Parse the time and save the hours and minutes.
        String[] split = time.split(":");
        hour = Integer.parseInt(split[0]);
        minute = Integer.parseInt(split[1]);
    }

    /**
     * Saves the time into the persisted storage ( shared preferences )
     */
    private void saveTime(){
        // Save the hour and minute in a HH:mm format ( 13:30 )
        persistString(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
    }

    /**
     * Called when the time is selected from the time dialog
     * @param spinner time spinner that was changed
     */
    @Override
    public void onTimeSelect(TimeSpinner spinner) {
        // Reload the hour and minute from the spinner
        hour = spinner.getHour();
        minute = spinner.getMinute();

        // Save the changed time
        saveTime();
    }
}
