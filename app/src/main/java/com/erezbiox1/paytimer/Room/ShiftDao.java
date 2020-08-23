package com.erezbiox1.paytimer.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ShiftDao {

    @Insert
    void insert(Shift shift);

    @Delete
    void delete(Shift shift);

    @Update
    void update(Shift shift);

    @Query("SELECT * FROM shifts_table ORDER BY startTime")
    LiveData<List<Shift>> getAllShifts();

}
