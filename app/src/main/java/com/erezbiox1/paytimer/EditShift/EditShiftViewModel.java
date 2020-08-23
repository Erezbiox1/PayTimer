package com.erezbiox1.paytimer.EditShift;

import android.app.Application;
import android.util.Log;

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

    private MutableLiveData<String> goBack = new MutableLiveData<>();

    private ShiftRepository repository;
    private CombinedSpinner start, end;
    private int currentShift;

    public EditShiftViewModel(@NonNull Application application) {
        super(application);
        repository = new ShiftRepository(application);

        start = new CombinedSpinner();
        end = new CombinedSpinner();
    }

    void setEditing(int id){
        currentShift = id;
    }

    void setSpinners(DateSpinner startDate,
                     TimeSpinner startTime,
                     DateSpinner endDate,
                     TimeSpinner endTime){
        start.setSpinners(startDate, startTime);
        end.setSpinners(endDate, endTime);
    }

    void save(){
        if(start.getTime() >= end.getTime() || end.getTime() > new Date().getTime()){
            error("Invalid times.");
            return;
        }

        if(currentShift == -1) {
            Shift shift = new Shift(start.getTime(), end.getTime());
            repository.insert(shift);
        } else {
            Shift shift = new Shift(currentShift, start.getTime(), end.getTime());
            repository.update(shift);
        }

        goBack();
    }

    private void goBack(){
        goBack.setValue("OK");
    }

    private void error(String error){
        goBack.setValue(error);
    }

    LiveData<String> getGoBack() {
        return goBack;
    }
}
