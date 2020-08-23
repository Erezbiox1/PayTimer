package com.erezbiox1.paytimer.EditShift.Spinners;

import java.lang.ref.WeakReference;
import java.util.Calendar;

public class CombinedSpinner implements DateSpinner.OnSpinnerDateSelected, TimeSpinner.OnSpinnerTimeSelect {

    private WeakReference<DateSpinner> date;
    private WeakReference<TimeSpinner> time;

    private int year, month, day, hour, minute;

    public CombinedSpinner() {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
    }

    public CombinedSpinner(int year, int month, int day, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public void setSpinners(DateSpinner date, TimeSpinner time){
        this.date = new WeakReference<>(date);
        this.time = new WeakReference<>(time);

        date.setYear(year);
        date.setMonth(month);
        date.setDay(day);
        time.setHour(hour);
        time.setMinute(minute);

        date.setOnSpinnerDateSelected(this);
        time.setOnSpinnerTimeSelect(this);
    }


    @Override
    public void onDateSelected(DateSpinner spinner) {
        year = spinner.getYear();
        month = spinner.getMonth();
        day = spinner.getDay();
    }

    @Override
    public void onTimeSelect(TimeSpinner spinner) {
        hour = spinner.getHour();
        minute = spinner.getMinute();
    }

    public long getTime(){
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, hour, minute);
        return c.getTime().getTime();
    }
}
