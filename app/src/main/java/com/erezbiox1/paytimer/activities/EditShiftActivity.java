/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.erezbiox1.paytimer.BuildConfig;
import com.erezbiox1.paytimer.database.ShiftRepository;
import com.erezbiox1.paytimer.utils.Utils;
import com.erezbiox1.paytimer.viewmodel.EditShiftViewModel;
import com.erezbiox1.paytimer.views.spinners.CombinedSpinner;
import com.erezbiox1.paytimer.views.spinners.DateSpinner;
import com.erezbiox1.paytimer.views.spinners.TimeSpinner;
import com.erezbiox1.paytimer.R;
import com.erezbiox1.paytimer.model.Shift;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class EditShiftActivity extends AppCompatActivity implements Observer<Shift> {

    // Intent extra's constants.
    public static final String START_TIME_EXTRA = BuildConfig.APPLICATION_ID + ".extra.TIME_START";
    public static final String END_TIME_EXTRA = BuildConfig.APPLICATION_ID + ".extra.TIME_END";
    public static final String SHIFT_ID_EXTRA = BuildConfig.APPLICATION_ID + ".extra.SHIFT_ID";

    // The viewModel, and the local viewModel shift ( same reference, will be saved after fab click )
    private EditShiftViewModel viewModel;
    private Shift shift = null;

    // Local UI elements ( combined spinner of date and time, both for the start datetime and the end datetime. tip and hourlyRate editTexts )
    private CombinedSpinner start, end;
    private TextInputLayout editTip, editHourlyRate;
    private Button[] quickShifts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shift);

        // Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView view = findViewById(R.id.add_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Load Local Views
        editTip = findViewById(R.id.edit_tip);
        editHourlyRate = findViewById(R.id.edit_hourly_rate);

        // Create and update the combined spinners

        start = new CombinedSpinner();
        start.setSpinners(
                (DateSpinner) findViewById(R.id.start_date),
                (TimeSpinner) findViewById(R.id.start_time));

        end = new CombinedSpinner();
        end.setSpinners(
                (DateSpinner) findViewById(R.id.end_date),
                (TimeSpinner) findViewById(R.id.end_time));

        // Get the viewModel, observe changes made to it's local shift ( will
        // only be called when asked or when the shift is being saved. )
        viewModel = ViewModelProviders.of(this).get(EditShiftViewModel.class);
        viewModel.observe(this, this);

        // Get the optional ID from the intent ( -1 for "no id" )
        int id = getIntent().getIntExtra(SHIFT_ID_EXTRA, -1);

        if(id == -1) {
            // Get the starting and ending time from the intent extras.
            long startTime = getIntent().getLongExtra(START_TIME_EXTRA, System.currentTimeMillis());
            long endTime = getIntent().getLongExtra(END_TIME_EXTRA, System.currentTimeMillis());

            // Ask the viewModel for the updated shift ( either the one saved in it's memory
            // or a new one composed of these provided starting and ending times. )
            viewModel.syncShift(startTime, endTime);
        } else {
            // Ask the viewModel for the updated shift ( either the one saved in it's memory
            // or ask the viewModel to load the specified shift from the database. )
            viewModel.syncShift(id);
        }

        // Set the title bar accordingly
        view.setText(id == -1 ? R.string.add_title : R.string.edit_title);

        // Set the fab save button functionality ( saves the shift by calling the viewModel )
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            viewModel.save();
        });

        // Event listener that will listen to the viewModel in case of an error or a finish signal.
        viewModel.getGoBack().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                // The activity is finished, can go back to the parent activity.
                if(message.equals("OK"))
                    finish();

                // an "INVALID_TIMES" error has occurred, notify the user that their times are not correct.
                if(message.equals("INVALID_TIMES"))
                    Toast.makeText(EditShiftActivity.this, R.string.invalid_times, Toast.LENGTH_LONG).show();
            }
        });

        quickShifts = new Button[]{
                findViewById(R.id.quick_shift_button1),
                findViewById(R.id.quick_shift_button2),
                findViewById(R.id.quick_shift_button3),
                findViewById(R.id.quick_shift_button4)
        };

        List<Double> latest4 = Utils.getLatestLengths(this);

        for (int i = 0; i < quickShifts.length; i++) {
            final double length = latest4.get(i);

            quickShifts[i].setText(getString(R.string.quick_shift_length, latest4.get(i)));
            quickShifts[i].setOnClickListener(v -> {
                start.setTime((long) (end.getTime() - 3600000 * length));
            });
        }

    }

    /**
     * A activity method that is called before configuration changes, when
     * called, save the changed shift's information in the viewModel local shift.
     * ( by calling saveChanges() it will update the same shift - same reference as
     *   local shift in the viewModel memory. )
     * @param outState saved sate
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        saveChanges();
        super.onSaveInstanceState(outState);
    }

    /**
     * Will listen to the new shift that was asked by the syncShift method.
     * @param shift the provided shift.
     */
    @Override
    public void onChanged(Shift shift) {
        if(this.shift == null) {
            // If the activity doesn't have any shift in memory ( aka shift is null ),
            // load the shift and update the UI to it's data.

            this.shift = shift;
            loadChanges();
        } else {
            // If the activity does have a shift in memory ( aka shift isn't null ),
            // Save the activity UI data in the shift.

            saveChanges();
        }
    }

    /**
     * Will save the UI data in the shift.
     */
    private void saveChanges(){
        shift.setStartTime(start.getTime());
        shift.setEndTime(end.getTime());
        shift.setHourlyRate(getHourlyRate());
        shift.setTip(getTip());
    }

    /**
     * Will load the shift data to the UI.
     */
    private void loadChanges(){
        start.setTime(shift.getStartTime());
        end.setTime(shift.getEndTime());
        editHourlyRate.getEditText().setText(new DecimalFormat("#.##").format(shift.getHourlyRate()));

        if (shift.getTip() != null){
            editTip.setVisibility(View.VISIBLE);

            if(shift.getTip() != 0)
                editTip.getEditText().setText(String.format(Locale.getDefault(), "%d", shift.getTip()));
        } else {
            editTip.setVisibility(View.GONE);
        }
    }

    /**
     * @return ui tip field
     */
    private int getTip(){
        String text = editTip.getEditText().getText().toString();
        if(text.isEmpty()) return 0;
        return Integer.parseInt(text);
    }

    /**
     * @return hourly rate ui field.
     */
    private double getHourlyRate(){
        String text = editHourlyRate.getEditText().getText().toString();
        if(text.isEmpty()) return 0;
        return Double.parseDouble(text);
    }
}
