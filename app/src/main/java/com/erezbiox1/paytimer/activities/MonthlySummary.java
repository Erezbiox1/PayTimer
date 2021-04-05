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

import android.view.View;
import android.widget.TextView;

import com.erezbiox1.paytimer.R;

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
    }
}