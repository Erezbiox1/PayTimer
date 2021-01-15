/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.views.spinners;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.DatePicker;

import java.util.Locale;

/**
 * A date spinner that extends custom spinner,
 * will show a date dialog when clicked and
 * will update the text to the date.
 */
public class DateSpinner extends CustomSpinner implements OnDateSetListener {

    // Listener that will listen for when the dialog will return a date.
    private OnSpinnerDateSelected onSpinnerDateSelected;

    // the year, month and day returned by the dialog
    private int year, month, day;

    // default constructor matching abstract super class
    public DateSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // Return a date picking dialog, an android provided dialog class.
    @Override
    public Dialog getDialog() {
        return new DatePickerDialog(getContext(), this, year, month, day);
    }

    // Listener for when the dialog will return a date.
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        // set the variables
        this.year = year;
        this.month = month;
        this.day = day;

        // call the listener, and update the spinner text.
        onSpinnerDateSelected.onDateSelected(this);
        updateText();
    }

    /**
     * Updates the spinner text using a day/month/year format ( 19/12/2002 )
     */
    private void updateText(){
        setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year));
    }

    /**
     * Set the listener
     * @param onSpinnerDateSelected listener
     */
    public void setOnSpinnerDateSelected(OnSpinnerDateSelected onSpinnerDateSelected) {
        this.onSpinnerDateSelected = onSpinnerDateSelected;
    }

    /**
     * Callback interface, will be called when the dialog finishes.
     */
    interface OnSpinnerDateSelected {
        void onDateSelected(DateSpinner spinner);
    }

    // Getters and setters.

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public void setYear(int year) {
        this.year = year;
        updateText();
    }

    public void setMonth(int month) {
        this.month = month;
        updateText();
    }

    public void setDay(int day) {
        this.day = day;
        updateText();
    }
}
