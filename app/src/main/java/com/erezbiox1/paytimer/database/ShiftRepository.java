/*
 * Copyright (c) 2021. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.erezbiox1.paytimer.database.firebase.FirebaseShiftRepository;
import com.erezbiox1.paytimer.database.sql.SqlShiftRepository;
import com.erezbiox1.paytimer.model.Shift;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public abstract class ShiftRepository {

    private static ShiftRepository INSTANCE;

    public abstract LiveData<List<Shift>> getAllShifts();

    public abstract LiveData<Shift> getShift(int id);

    public abstract void insert(Shift shift);

    public abstract void update(Shift shift);

    public abstract void delete(Shift shift);

    public static ShiftRepository getInstance(Context context){
        if(INSTANCE == null)
            INSTANCE = FirebaseAuth.getInstance().getCurrentUser() == null ? new SqlShiftRepository(context) : new FirebaseShiftRepository();;

        return INSTANCE;
    }

}
