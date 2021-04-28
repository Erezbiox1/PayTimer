/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.activities;

import android.content.Intent;
import android.os.Bundle;

import com.erezbiox1.paytimer.adaptors.ShiftsAdapter;
import com.erezbiox1.paytimer.adaptors.ShiftsAdapter.ListItem.ListItemType;
import com.erezbiox1.paytimer.database.ShiftRepository;
import com.erezbiox1.paytimer.model.Shift;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.erezbiox1.paytimer.R;

import java.util.ArrayList;
import java.util.List;

public class ListShiftsActivity extends AppCompatActivity implements Observer<List<Integer>> {

    private ShiftsAdapter shiftsAdapter;
    private List<Integer> selectedShifts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_shifts);

        // Setup the toolbar

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set the fab functionality ( will add a new shift )

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to the EditShiftActivity, passing no extras will make it add a new shift.
                Intent newShiftIntent = new Intent(ListShiftsActivity.this, EditShiftActivity.class);
                startActivity(newShiftIntent);
            }
        });

        // RecyclerView ( the shift's list ), and the no shift place holder.
        final RecyclerView recyclerView = findViewById(R.id.shifts_list);
        final TextView noShiftsView = findViewById(R.id.no_shifts);

        // The shift's adapter ( used to update each item with the shift's data. ) and repository ( used to fetch the list data )
        shiftsAdapter = new ShiftsAdapter();
        final ShiftRepository repository = ShiftRepository.getInstance(getApplication());

        // Listen the changes in the list repository, when changes occur, update the UI accordingly.
        repository.getAllShifts().observe(this, new Observer<List<Shift>>() {
            @Override
            public void onChanged(List<Shift> shifts) {
                if(shifts != null && shifts.size() > 0){
                    // If there are shifts in the DB, make the list visible and the place holder invisible.
                    recyclerView.setVisibility(View.VISIBLE);
                    noShiftsView.setVisibility(View.GONE);

                    // Update the shift's adapter with the updated list.
                    shiftsAdapter.setShiftsList(shifts);
                } else {
                    // If there are no shifts in the DB, make the list invisible and show the placeholder.
                    recyclerView.setVisibility(View.GONE);
                    noShiftsView.setVisibility(View.VISIBLE);
                }
            }
        });

        // Setup the recyclerview with our adapter, and android provided linear layout and default item animator.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(shiftsAdapter);

        // Add a scroll listener that will hide the fab after scrolling.
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy < 0)
                    fab.show();
                else if(dy > 0)
                    fab.hide();
            }
        });

        // The item touch helper that will listen to item swipes
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                // Call the shift deleteShift() method. ( will ask the user for confirmation then will delete the shift. )
                ((ShiftsAdapter.ShiftViewHolder) viewHolder).deleteShift();
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                // Get the item type
                ListItemType type = ListItemType.getType(viewHolder.getItemViewType());

                if(type == ListItemType.SHIFT)
                    return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);

                return 0;
            }
        });

        // Attach the swipe listener to the recyclerview
        helper.attachToRecyclerView(recyclerView);

        shiftsAdapter.selectedLiveData.observe(this, this);
    }

    /**
     * Inflates the options menu
     * @param menu menu to inflate
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(selectedShifts.size() > 0)
            getMenuInflater().inflate(R.menu.selected_shifts_menu, menu);
        else
            getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    /**
     * On option selected listener,
     * start the settings activity ( only item currently in the menu )
     * @param item menu item clicked
     * @return was the item clicked handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(selectedShifts.size() > 0){
            switch (item.getItemId()){
                case R.id.shift_selected_options_paid:
                    shiftsAdapter.getShiftsList()
                            .stream()
                            .filter(shift -> selectedShifts.contains(shift.getId()))
                            .forEach(shift -> {
                                shift.setPaid(!shift.isPaid());
                                ShiftRepository.getInstance(this).update(shift);
                    });

                    shiftsAdapter.selectedLiveData.setValue(new ArrayList<>());
                    break;
                case R.id.shift_selected_options_delete:
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.delete_shifts)
                            .setMessage(R.string.delete_shifts_text)
                            .setIcon(R.drawable.ic_warning)
                            .setPositiveButton(R.string.delete_shift_yes, (dialogInterface, i) -> {
                                // If the alert "yes" button was clicked, delete the shifts:
                                shiftsAdapter.getShiftsList()
                                        .stream()
                                        .filter(shift -> selectedShifts.contains(shift.getId()))
                                        .forEach(shift -> {
                                            // Tell the ShiftRepository to delete the shift asynchronously.
                                            ShiftRepository.getInstance(this).delete(shift);
                                        });

                                shiftsAdapter.selectedLiveData.setValue(new ArrayList<>());
                            })
                            .setNegativeButton(R.string.delete_shift_no, null)
                            .show();
                    break;
            }


        } else {
            if (item.getItemId() == R.id.action_settings) {
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onChanged(List<Integer> selectedShifts) {
        this.selectedShifts = selectedShifts;
        invalidateOptionsMenu();
    }
}
