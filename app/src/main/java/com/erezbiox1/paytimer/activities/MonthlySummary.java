/*
 * Copyright (c) 2021. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.erezbiox1.paytimer.BuildConfig;
import com.erezbiox1.paytimer.adaptors.ShiftsAdapter;
import com.erezbiox1.paytimer.database.ShiftRepository;
import com.erezbiox1.paytimer.model.Shift;
import com.erezbiox1.paytimer.utils.Utils;
import com.erezbiox1.paytimer.utils.Utils.Month;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.erezbiox1.paytimer.R;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class MonthlySummary extends AppCompatActivity implements Observer<List<Shift>> {

    public static final String MONTH_EXTRA = BuildConfig.APPLICATION_ID + ".extra.MONTH";
    private Month month;
    private List<Shift> shifts;

    private TextView sumText, monthText, hoursText, shiftsText;
    private View[] weekdayViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_summary);

        // Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        month = getIntent().getParcelableExtra(MONTH_EXTRA);

        ShiftRepository.getInstance(this).getAllShifts().observe(this, this);

        sumText = findViewById(R.id.total_earnings);
        monthText = findViewById(R.id.month);
        hoursText = findViewById(R.id.report_total_hours);
        shiftsText = findViewById(R.id.report_total_shifts);

        weekdayViews = new View[]{
                findViewById(R.id.stat_day1),
                findViewById(R.id.stat_day2),
                findViewById(R.id.stat_day3),
                findViewById(R.id.stat_day4),
                findViewById(R.id.stat_day5),
                findViewById(R.id.stat_day6),
                findViewById(R.id.stat_day7),
        };
    }

    @Override
    public void onChanged(List<Shift> shifts) {
        this.shifts = shifts
                .stream()
                .filter(shift -> Month.getMonth(shift.getStartTime()).equals(month))
                .collect(Collectors.toList());

        updateUi();
    }

    @SuppressLint("SetTextI18n")
    private void updateUi(){
        LongStream totalHours = shifts.stream().mapToLong(Shift::getTotalHours);
        DoubleStream totalPay = shifts.stream().mapToDouble(Shift::getTotalPay);

        monthText.setText(month.toString());
        sumText.setText(Utils.getFormattedTotalPayout(this, totalPay.sum()));
        shiftsText.setText(getString(R.string.monthly_report_total_shifts, shifts.size()));
        hoursText.setText(getString(R.string.monthly_report_total_hours, (int) totalHours.sum() / 3600000));

        int[] weekdays = new int[7];
        for (Shift shift : shifts) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(shift.getStartTime());

            int weekday = c.get(Calendar.DAY_OF_WEEK);
            weekdays[weekday - 1] += shift.getTotalPay();
        }

        calcWeekdaysHeights(weekdays);

        for (int i = 0; i < weekdays.length; i++) {

        }

        for (int i = 0; i < weekdayViews.length; i++) {
            Log.i("MonthlySummary", i + ": " + weekdays[i]);
            weekdayViews[i].getLayoutParams().height = weekdays[i];
            weekdayViews[i].requestLayout();
        }
    }

    private void calcWeekdaysHeights(int[] weekdays){
        final double INITIAL = 30.0, MAX = 80.0;

        int max = -1;
        for(int weekday: weekdays)
            if(weekday > max) max = weekday;

        for (int i = 0; i < weekdays.length; i++)
            weekdays[i] = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) (INITIAL + ((double) weekdays[i] / max) * MAX), getResources().getDisplayMetrics());

    }
}