package com.erezbiox1.paytimer.AddShift;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erezbiox1.paytimer.AddShift.Spinners.CombinedSpinner;
import com.erezbiox1.paytimer.AddShift.Spinners.DateSpinner;
import com.erezbiox1.paytimer.AddShift.Spinners.TimeSpinner;
import com.erezbiox1.paytimer.Room.Shift;
import com.erezbiox1.paytimer.Room.ShiftRepository;

public class AddShiftViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> goBack = new MutableLiveData<>();

    private ShiftRepository repository;
    private CombinedSpinner start, end;

    public AddShiftViewModel(@NonNull Application application) {
        super(application);
        repository = new ShiftRepository(application);

        start = new CombinedSpinner();
        end = new CombinedSpinner();
    }

    public void setSpinners(DateSpinner startDate,
                            TimeSpinner startTime,
                            DateSpinner endDate,
                            TimeSpinner endTime){
        start.setSpinners(startDate, startTime);
        end.setSpinners(endDate, endTime);
    }

    public void add(){
        Shift shift = new Shift(start.getTime(), end.getTime());
        repository.insert(shift);
        goBack.setValue(true);
    }

    public LiveData<Boolean> getGoBack() {
        return goBack;
    }
}
