/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.EditShift;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erezbiox1.paytimer.EditShift.Spinners.CombinedSpinner;
import com.erezbiox1.paytimer.EditShift.Spinners.DateSpinner;
import com.erezbiox1.paytimer.EditShift.Spinners.TimeSpinner;
import com.erezbiox1.paytimer.Room.Shift;
import com.erezbiox1.paytimer.Room.ShiftRepository;

import java.util.Date;

public class EditShiftViewModel extends AndroidViewModel {

    // An observer used to communicate with the activity,
    // either to pass an error or to tell it to go back to the main activity ( finish() )
    private MutableLiveData<String> goBack = new MutableLiveData<>();

    // The shift's repo, the start and end combined spinners, and the current shifts id.
    private ShiftRepository repository;
    private CombinedSpinner start, end;
    private int currentShift;

    // Required Constructor matching super constructor. sets the repository and the combined listeners.
    public EditShiftViewModel(@NonNull Application application) {
        super(application);
        repository = new ShiftRepository(application);

        start = new CombinedSpinner();
        end = new CombinedSpinner();
    }

    /**
     * Sets the current shift id that is being edited.
     * @param id
     */
    void setEditing(int id){
        currentShift = id;
    }

    /**
     * Pass the activity provided spinner elements to the combined spinners
     * @param startDate
     * @param startTime
     * @param endDate
     * @param endTime
     */
    void setSpinners(DateSpinner startDate,
                     TimeSpinner startTime,
                     DateSpinner endDate,
                     TimeSpinner endTime){
        start.setSpinners(startDate, startTime);
        end.setSpinners(endDate, endTime);
    }

    /**
     * Set the starting and ending time in the combined spinners.
     * @param startTime
     * @param endTime
     */
    void setTime(long startTime, long endTime){
        start.setTime(startTime);
        end.setTime(endTime);
    }

    /**
     * Saves the shift.
     */
    void save(){
        // if the starting time is later or equal to the ending time, error out.
        // if the ending time is later then the current time, error out.
        if(start.getTime() >= end.getTime() || end.getTime() > new Date().getTime()){
            error("Invalid times.");
            return;
        }

        // if the current id is -1 ( id not set ), create ( and insert )
        // a new shift, otherwise update the specified shift.
        if(currentShift == -1) {
            Shift shift = new Shift(start.getTime(), end.getTime());
            repository.insert(shift);
        } else {
            Shift shift = new Shift(currentShift, start.getTime(), end.getTime());
            repository.update(shift);
        }

        // Tell the activity it call finish and go back to the previous activity
        goBack();
    }

    /**
     * Tell the activity it call finish and go back to the previous activity
     */
    private void goBack(){
        goBack.setValue("OK");
    }

    /**
     * Tell the activity to display the said error message.
     */
    private void error(String error){
        goBack.setValue(error);
    }

    /**
     * @return An observer used to communicate with the activity,
     *         either to pass an error or to tell it to go back to
     *         the main activity ( finish() )
     */
    LiveData<String> getGoBack() {
        return goBack;
    }
}
