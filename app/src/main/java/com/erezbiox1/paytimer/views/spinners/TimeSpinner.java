/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.views.spinners;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TimePicker;

import java.util.Locale;

/**
 * A Time spinner that extends custom spinner,
 * will show a time dialog when clicked and
 * will update the text to the time.
 */
public class TimeSpinner extends CustomSpinner implements OnTimeSetListener {

    // Listener that will listen for when the dialog will return a time.
    private OnSpinnerTimeSelect onSpinnerTimeSelect;

    // The hour and minute returned by the dialog
    private int hour, minute;

    // default constructor matching abstract super class
    public TimeSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // Return a time picking dialog, an android provided dialog class.
    @Override
    public Dialog getDialog() {
        return new TimePickerDialog(getContext(), this, hour, minute, DateFormat.is24HourFormat(getContext()));
    }

    // Listener for when the dialog will return a time.
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        // set the variables
        this.hour = hour;
        this.minute = minute;

        // call the listener, and update the spinner text.
        onSpinnerTimeSelect.onTimeSelect(this);
        updateText();
    }

    /**
     * Updates the spinner text using a hour:minute format ( 12:30 )
     */
    private void updateText(){
        setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
    }

    /**
     * Sets the listener
     * @param onSpinnerTimeSelect listener
     */
    public void setOnSpinnerTimeSelect(OnSpinnerTimeSelect onSpinnerTimeSelect) {
        this.onSpinnerTimeSelect = onSpinnerTimeSelect;
    }

    /**
     * Callback interface, will be called when the dialog finishes.
     */
    public interface OnSpinnerTimeSelect {
        void onTimeSelect(TimeSpinner spinner);
    }

    // Getters and setters.

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setHour(int hour) {
        this.hour = hour;
        updateText();
    }

    public void setMinute(int minute) {
        this.minute = minute;
        updateText();
    }
}
