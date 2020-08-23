package com.erezbiox1.paytimer.AddShift;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.erezbiox1.paytimer.AddShift.Spinners.DateSpinner;
import com.erezbiox1.paytimer.AddShift.Spinners.TimeSpinner;
import com.erezbiox1.paytimer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddShiftActivity extends AppCompatActivity {

    private AddShiftViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shift);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewModel = ViewModelProviders.of(this).get(AddShiftViewModel.class);

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
                viewModel.add();
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
