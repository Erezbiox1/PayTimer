package com.erezbiox1.paytimer.EditShift;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.erezbiox1.paytimer.BuildConfig;
import com.erezbiox1.paytimer.EditShift.Spinners.DateSpinner;
import com.erezbiox1.paytimer.EditShift.Spinners.TimeSpinner;
import com.erezbiox1.paytimer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditShiftActivity extends AppCompatActivity {

    public static final String START_TIME_EXTRA = BuildConfig.APPLICATION_ID + ".extra.TIME_START"; // TODO
    public static final String END_TIME_EXTRA = BuildConfig.APPLICATION_ID + ".extra.TIME_END"; // TODO
    public static final String SHIFT_ID_EXTRA = BuildConfig.APPLICATION_ID + ".extra.SHIFT_ID";

    private EditShiftViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shift);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewModel = ViewModelProviders.of(this).get(EditShiftViewModel.class);

        int id = getIntent().getIntExtra(SHIFT_ID_EXTRA, -1);
        viewModel.setEditing(id);

        viewModel.setSpinners(
                (DateSpinner) findViewById(R.id.start_date),
                (TimeSpinner) findViewById(R.id.start_time),
                (DateSpinner) findViewById(R.id.end_date),
                (TimeSpinner) findViewById(R.id.end_time)
        );

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.save();
            }
        });

        viewModel.getGoBack().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean)
                    finish();
            }
        });

    }

}
