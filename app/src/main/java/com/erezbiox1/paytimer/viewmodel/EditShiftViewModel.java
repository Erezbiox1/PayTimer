/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

import com.erezbiox1.paytimer.database.ShiftRepository;
import com.erezbiox1.paytimer.model.Shift;
import com.erezbiox1.paytimer.database.sql.SqlShiftRepository;

import java.util.Date;

public class EditShiftViewModel extends AndroidViewModel implements Observer<Shift> {

    // Shift live data, used to notify the activity of shift changes.
    private MutableLiveData<Shift> activityShift = new MutableLiveData<>();

    // Event listener, used to notify the activity to show the user an error or to finish the activity.
    private MutableLiveData<String> goBack = new MutableLiveData<>();

    // The shift's repository, database shift liveData ( used to deregister the viewModel as a listener )
    private ShiftRepository repository;
    private LiveData<Shift> shiftLiveData;

    // Local shift that will be preserved until the activity dies or the
    // shift will be saved. ( also weather that shift is new or not )
    private boolean newShift;
    private Shift shift;

    /**
     * EditShiftViewModel Constructor
     * @param application provided application
     */
    public EditShiftViewModel(@NonNull Application application) {
        super(application);

        // Create the ShiftRepository from the provided application context.
        repository = ShiftRepository.getInstance(getApplication());
    }

    /**
     * a method used by the activity to pass it's provided shift id,
     * and ask for the specified activity.
     * @param id
     */
    public void syncShift(int id){
        // Mark that the shift isn't new ( we know it's id )
        this.newShift = false;

        if(shift != null)
            // If the shift is in memory ( if the activity had a configuration change for example )
            // return that shift.

            activityShift.setValue(shift);
        else{
            // otherwise, ask the database to update the viewModel as soon as possible
            // with the specified shift. Save the liveData so after the viewModel
            // get's that shift the viewModel can deregister itself.

            shiftLiveData = repository.getShift(id);
            shiftLiveData.observeForever(this);
        }
    }

    /**
     * a method used by the activity to pass it's provided starting and ending time,
     * and ask for the a new activity with those times.
     * @param startTime shift's starting time
     * @param endTime shift's ending time
     */
    public void syncShift(long startTime, long endTime){
        // If the shift is in memory ( if the activity had a configuration change for example )
        // return that shift.
        if(shift != null){
            activityShift.setValue(shift);
            return;
        }

        // Otherwise, create a new shift.

        // Get the user defined settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());

        // Get the default hourly pay.
        Double hourlyRate = Double.valueOf(prefs.getString("hourly_pay", "24"));

        // Check if the tip is enabled ( if so then set it to 0, otherwise null. )
        Integer tip = prefs.getBoolean("tips_enabled", false) ? 0 : null;

        // Create the new shift from the collected parameters. Mark that the shift is new.
        this.shift = new Shift(startTime, endTime, hourlyRate, tip);
        this.newShift = true;

        // Pass the shift to the activity.
        activityShift.setValue(shift);
    }

    /**
     * Called from the database ( when the liveData changes )
     * when the requested shift is available.
     * Pass that shift to the activity and deregister the viewModel.
     * @param shift provided shift
     */
    @Override
    public void onChanged(Shift shift) {
        // Save the provided shift in memory
        this.shift = shift;

        // Pass the shift to the activity so it can update it's UI.
        activityShift.setValue(shift);

        // Remove the viewModel as an observer for the shift's liveData.
        shiftLiveData.removeObserver(this);
    }

    /**
     * will be used by the activity to register for shift changes.
     * after registering the activity will ask for a shift using syncShift()
     * and will get it's result using this observer.
     * @param lifecycleOwner the activity
     * @param observer also the activity
     */
    public void observe(LifecycleOwner lifecycleOwner, Observer<Shift> observer){
        activityShift.observe(lifecycleOwner, observer);
    }

    /**
     * Saves the shift.
     */
    public void save(){

        // force the shift to save it's UI data to the local shift ( by passing the same shift again )
        activityShift.setValue(shift);

        // if the starting time is later or equal to the ending time, error out.
        // if the ending time is later then the current time, error out.
        if(shift.getStartTime() >= shift.getEndTime() || shift.getEndTime() > new Date().getTime()){
            goBack.setValue("INVALID_TIMES");
            return;
        }

        // If the shift is new, insert it to the database,
        // else, update the database with the updated shift.
        if(newShift)
            repository.insert(shift);
        else
            repository.update(shift);

        // Tell the activity it call finish and go back to the previous activity
        goBack.setValue("OK");
    }

    /**
     * @return An observer used to communicate with the activity,
     *         either to pass an error or to tell it to go back to
     *         the main activity ( finish() )
     */
    public LiveData<String> getGoBack() {
        return goBack;
    }

}
