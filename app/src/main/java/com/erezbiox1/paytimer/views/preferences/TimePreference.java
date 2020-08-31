/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.views.preferences;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import com.erezbiox1.paytimer.R;
import com.erezbiox1.paytimer.views.spinners.TimeSpinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimePreference extends Preference implements TimeSpinner.OnSpinnerTimeSelect {

    private TimeSpinner timeSpinner;
    private int hour, minute;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWidgetLayoutResource(R.layout.pref_time);

        loadTime();
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        holder.itemView.setClickable(false);
        timeSpinner = (TimeSpinner) holder.findViewById(R.id.time_pref_spinner);

        timeSpinner.setHour(hour);
        timeSpinner.setMinute(minute);
        timeSpinner.setOnSpinnerTimeSelect(this);
    }

    private void loadTime(){
        String defaultTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(System.currentTimeMillis());
        String time = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(getKey(), defaultTime);
        String[] split = time.split(":");
        hour = Integer.parseInt(split[0]);
        minute = Integer.parseInt(split[1]);
    }

    private void saveTime(){
        hour = timeSpinner.getHour();
        minute = timeSpinner.getMinute();
        persistString(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
    }

    @Override
    public void onTimeSelect(TimeSpinner spinner) {
        saveTime();
    }
}
