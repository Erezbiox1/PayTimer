/*
 * Copyright (c) 2021. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.database.sql;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.erezbiox1.paytimer.model.Shift;

import java.util.List;

@Dao
public interface SqlShiftDao {

    /**
     * Inserts a new shift into the local database
     * @param shift
     */
    @Insert
    void insert(Shift shift);

    /**
     * Deletes a shift from the local database
     * @param shift
     */
    @Delete
    void delete(Shift shift);

    /**
     * Updates a shift in the local database
     * @param shift
     */
    @Update
    void update(Shift shift);

    @Query("SELECT * FROM shifts_table WHERE id = :id")
    LiveData<Shift> getShift(int id);

    /**
     * Gets a LiveData of all of the shifts. updates live as the database changes.
     * @return
     */
    @Query("SELECT * FROM shifts_table ORDER BY startTime")
    LiveData<List<Shift>> getAllShifts();

}
