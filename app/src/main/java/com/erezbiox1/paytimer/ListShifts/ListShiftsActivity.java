/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.ListShifts;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.erezbiox1.paytimer.EditShift.EditShiftActivity;
import com.erezbiox1.paytimer.Room.Shift;
import com.erezbiox1.paytimer.Room.ShiftRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.erezbiox1.paytimer.R;

import java.util.List;

public class ListShiftsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_shifts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newShiftIntent = new Intent(ListShiftsActivity.this, EditShiftActivity.class);
                startActivity(newShiftIntent);
            }
        });

        final RecyclerView recyclerView = findViewById(R.id.shifts_list);

        final ShiftsAdapter shiftsAdapter = new ShiftsAdapter();
        final ShiftRepository repository = new ShiftRepository(getApplication());

        repository.getAllShifts().observe(this, new Observer<List<Shift>>() {
            @Override
            public void onChanged(List<Shift> shifts) {
                if(shifts != null)
                    shiftsAdapter.setShiftsList(shifts);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(shiftsAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy < 0)
                    fab.show();
                else if(dy > 0)
                    fab.hide();
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                ((ShiftsAdapter.ViewHolder) viewHolder).deleteShift();
                //shiftsAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
            }
        });

        helper.attachToRecyclerView(recyclerView);

    }

}
