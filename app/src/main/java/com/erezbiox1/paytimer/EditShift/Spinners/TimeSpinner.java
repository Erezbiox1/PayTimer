package com.erezbiox1.paytimer.EditShift.Spinners;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TimePicker;

import java.util.Locale;

public class TimeSpinner extends CustomSpinner implements OnTimeSetListener {

    private OnSpinnerTimeSelect onSpinnerTimeSelect;
    private int hour = 0, minute = 0;

    public TimeSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Dialog getDialog() {
        return new TimePickerDialog(getContext(), this, hour, minute, DateFormat.is24HourFormat(getContext()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        this.hour = hour;
        this.minute = minute;

        onSpinnerTimeSelect.onTimeSelect(this);
        updateText();
    }

    private void updateText(){
        setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
    }

    public void setOnSpinnerTimeSelect(OnSpinnerTimeSelect onSpinnerTimeSelect) {
        this.onSpinnerTimeSelect = onSpinnerTimeSelect;
    }

    interface OnSpinnerTimeSelect {
        void onTimeSelect(TimeSpinner spinner);
    }

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
