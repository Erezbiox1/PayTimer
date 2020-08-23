package com.erezbiox1.paytimer.Room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shifts_table")
public class Shift {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @NonNull
    private Long startTime;

    @NonNull
    private Long endTime;

    public Shift(@NonNull Long startTime, @NonNull Long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    @NonNull
    public Long getStartTime() {
        return startTime;
    }

    @NonNull
    public Long getEndTime() {
        return endTime;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
