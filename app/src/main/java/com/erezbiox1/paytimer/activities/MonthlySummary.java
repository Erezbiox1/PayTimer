/*
 * Copyright (c) 2021. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.erezbiox1.paytimer.BuildConfig;
import com.erezbiox1.paytimer.database.ShiftRepository;
import com.erezbiox1.paytimer.model.Shift;
import com.erezbiox1.paytimer.utils.Utils;
import com.erezbiox1.paytimer.utils.Utils.Month;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.erezbiox1.paytimer.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MonthlySummary extends AppCompatActivity implements Observer<List<Shift>> {

    public static final String MONTH_EXTRA = BuildConfig.APPLICATION_ID + ".extra.MONTH";
    private Month month;
    private List<Shift> shifts;

    private TextView sumText, monthText, hoursText, shiftsText, avgShiftText, avgTipText;
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

        // TODO: Create a filter that will fetch only the shifts from the same month.
        ShiftRepository.getInstance(this).getAllShifts().observe(this, this);

        sumText = findViewById(R.id.total_earnings);
        monthText = findViewById(R.id.month);
        hoursText = findViewById(R.id.report_total_hours);
        shiftsText = findViewById(R.id.report_total_shifts);

        avgTipText = findViewById(R.id.avg_tip_title);
        avgShiftText = findViewById(R.id.avg_shift_title);

        weekdayViews = new View[]{
                findViewById(R.id.stat_day1),
                findViewById(R.id.stat_day2),
                findViewById(R.id.stat_day3),
                findViewById(R.id.stat_day4),
                findViewById(R.id.stat_day5),
                findViewById(R.id.stat_day6),
                findViewById(R.id.stat_day7),
        };

        final TextView avgTipSubtitle = findViewById(R.id.avg_tip_sub_title);
        avgTipSubtitle.setText(Utils.getCurrencySymbol(this));
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

        double sum = shifts.stream().mapToDouble(Shift::getTotalPay).sum();
        int shiftCount = shifts.size();
        int totalHours = (int) shifts.stream().mapToLong(Shift::getTotalHours).sum() / 3600000;
        double avgShift = shifts.stream().mapToLong(Shift::getTotalHours).average().orElse(0)  / 3600000;
        int avgTip = (int) shifts.stream().mapToInt(Shift::getTip).average().orElse(0);

        monthText.setText(month.toString());
        sumText.setText(Utils.getFormattedTotalPayout(this, sum));
        shiftsText.setText(getString(R.string.monthly_report_total_shifts, shiftCount));
        hoursText.setText(getString(R.string.monthly_report_total_hours, totalHours));

        avgShiftText.setText(String.format(Locale.getDefault(), "%.1f", avgShift));
        avgTipText.setText(String.valueOf(avgTip));

        double[] weekdays = new double[7];
        for (Shift shift : shifts) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(shift.getStartTime());

            int weekday = c.get(Calendar.DAY_OF_WEEK);
            weekdays[weekday - 1] += shift.getTotalPay();
        }

        calcWeekdaysHeights(weekdays);
        setWeekdaysHeights(weekdays);
    }

    private void calcWeekdaysHeights(double[] weekdays){
        final double INITIAL = 30.0, MAX = 80.0;

        double max = -1;
        for(double weekday: weekdays)
            if(weekday > max) max = weekday;

        for (int i = 0; i < weekdays.length; i++)
            weekdays[i] = INITIAL + (weekdays[i] / max) * MAX;
    }

    private void setWeekdaysHeights(double[] weekdays){
        for (int i = 0; i < weekdayViews.length; i++) {
            weekdayViews[i].getLayoutParams().height = calcDP(weekdays[i]);
            weekdayViews[i].requestLayout();
        }
    }

    private int calcDP(double height){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) height, getResources().getDisplayMetrics());
    }
}