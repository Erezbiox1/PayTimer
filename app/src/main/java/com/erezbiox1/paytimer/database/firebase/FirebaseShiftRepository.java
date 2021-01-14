/*
 * Copyright (c) 2021. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.database.firebase;

import android.content.Context;
import android.util.Log;
import android.view.contentcapture.DataShareWriteAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erezbiox1.paytimer.database.ShiftRepository;
import com.erezbiox1.paytimer.model.Shift;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FirebaseShiftRepository extends ShiftRepository implements ValueEventListener {

    private final MutableLiveData<List<Shift>> allShifts = new MutableLiveData<>();
    private final DatabaseReference database;
    private final FirebaseUser user;

    private Map<Integer, String> idMap;

    public FirebaseShiftRepository() {
        this.database = FirebaseDatabase.getInstance().getReference();
        this.user = FirebaseAuth.getInstance().getCurrentUser();

        database.child("shifts").child(user.getUid()).addValueEventListener(this);

        // TODO: REMOVE: (empty array list)
        allShifts.setValue(new ArrayList<Shift>());
    }

    @Override
    public LiveData<List<Shift>> getAllShifts() {
        return allShifts;
    }

    @Override
    public LiveData<Shift> getShift(final int id) {
        final MutableLiveData<Shift> shiftLiveData = new MutableLiveData<>();
        database.child("shifts").child(user.getUid()).child(idMap.get(id)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Shift shift = snapshot.getValue(Shift.class);
                shift.setId(id);
                shiftLiveData.setValue(shift);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        return shiftLiveData;
    }

    @Override
    public void insert(Shift shift) {
        database.child("shifts").child(user.getUid()).push().setValue(shift.toMap());
    }

    @Override
    public void update(Shift shift) {
        database.child("shifts").child(user.getUid()).child(idMap.get(shift.getId())).setValue(shift.toMap());
    }

    @Override
    public void delete(Shift shift) {
        database.child("shifts").child(user.getUid()).child(idMap.get(shift.getId())).removeValue();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        List<Shift> shifts = new ArrayList<>();
        idMap = new HashMap<>();

        for (DataSnapshot entry : snapshot.getChildren()) {
            Shift shift = entry.getValue(Shift.class);

            int id = entry.getKey().hashCode();
            shift.setId(id);
            idMap.put(id, entry.getKey());

            shifts.add(shift);
        }

        allShifts.setValue(shifts);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        error.toException().printStackTrace();
    }
}
