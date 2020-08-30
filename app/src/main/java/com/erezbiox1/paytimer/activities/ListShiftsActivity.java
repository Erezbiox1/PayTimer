/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.activities;

import android.content.Intent;
import android.os.Bundle;

import com.erezbiox1.paytimer.adaptors.ShiftsAdapter;
import com.erezbiox1.paytimer.model.Shift;
import com.erezbiox1.paytimer.database.ShiftRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
        final TextView noShiftsView = findViewById(R.id.no_shifts);

        final ShiftsAdapter shiftsAdapter = new ShiftsAdapter();
        final ShiftRepository repository = new ShiftRepository(getApplication());

        repository.getAllShifts().observe(this, new Observer<List<Shift>>() {
            @Override
            public void onChanged(List<Shift> shifts) {
                if(shifts != null && shifts.size() > 0){
                    recyclerView.setVisibility(View.VISIBLE);
                    noShiftsView.setVisibility(View.GONE);

                    shiftsAdapter.setShiftsList(shifts);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noShiftsView.setVisibility(View.VISIBLE);
                }
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
            }
        });

        helper.attachToRecyclerView(recyclerView);

    }

    /**
     * Inflates the options menu
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * On option selected listener,
     * @param item
     * @return was the item clicked handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
