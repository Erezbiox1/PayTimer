/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.views.spinners;

import com.erezbiox1.paytimer.viewmodel.EditShiftViewModel;

import java.lang.ref.WeakReference;
import java.util.Calendar;

/**
 * A combined spinner used by the {@link EditShiftViewModel}
 * to manage the time and date spinners
 */
public class CombinedSpinner implements DateSpinner.OnSpinnerDateSelected, TimeSpinner.OnSpinnerTimeSelect {

    // Weak references to the date and time spinners, "weak"
    // so the could be garbage collected when the activity is destroyed
    private WeakReference<DateSpinner> date;
    private WeakReference<TimeSpinner> time;

    // year, month, day, hour and minute returned by the spinners.
    private int year, month, day, hour, minute;

    /**
     * A constructor that sets the date variables to the current time.
     */
    public CombinedSpinner() {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
    }

    /**
     * Update the weak references to the new spinners provided by the activity,
     * set their text, and register for callbacks.
     * @param date the date spinner
     * @param time the time spinner
     */
    public void setSpinners(DateSpinner date, TimeSpinner time){
        // Set the weak references to the new spinners
        this.date = new WeakReference<>(date);
        this.time = new WeakReference<>(time);

        // Set their text
        date.setYear(year);
        date.setMonth(month);
        date.setDay(day);
        time.setHour(hour);
        time.setMinute(minute);

        // Register for callbacks
        date.setOnSpinnerDateSelected(this);
        time.setOnSpinnerTimeSelect(this);
    }


    /**
     * the date spinner callback, called when the dialog returns a new date.
     * @param spinner date spinner that was modified by a user opened dialog
     */
    @Override
    public void onDateSelected(DateSpinner spinner) {
        year = spinner.getYear();
        month = spinner.getMonth();
        day = spinner.getDay();
    }

    /**
     * the date spinner callback, called when the dialog returns a new time.
     * @param spinner time spinner that was modified by a user opened dialog
     */
    @Override
    public void onTimeSelect(TimeSpinner spinner) {
        hour = spinner.getHour();
        minute = spinner.getMinute();
    }

    /**
     * Get the current spinner combined epoch time ( both date and time )
     * @return combined spinners epoch time.
     */
    public long getTime(){
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, hour, minute);
        return c.getTime().getTime();
    }

    /**
     * Set the time, and update the date and time spinners text
     * @param millis epoch time
     */
    public void setTime(long millis){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);

        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        // Update the spinners
        if(date.get() != null && time != null)
            setSpinners(date.get(), time.get());
    }
}
