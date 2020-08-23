package com.erezbiox1.paytimer.EditShift.Spinners;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.DatePicker;

import java.util.Locale;

public class DateSpinner extends CustomSpinner implements OnDateSetListener {

    private OnSpinnerDateSelected onSpinnerDateSelected;
    private int year, month, day;

    public DateSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Dialog getDialog() {
        return new DatePickerDialog(getContext(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;

        onSpinnerDateSelected.onDateSelected(this);
        updateText();
    }

    private void updateText(){
        setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month, year));
    }

    public void setOnSpinnerDateSelected(OnSpinnerDateSelected onSpinnerDateSelected) {
        this.onSpinnerDateSelected = onSpinnerDateSelected;
    }

    interface OnSpinnerDateSelected {
        void onDateSelected(DateSpinner spinner);
    }

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
