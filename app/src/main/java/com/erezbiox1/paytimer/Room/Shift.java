package com.erezbiox1.paytimer.Room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "shifts_table")
public class Shift {

    // Primary key, an auto generated integer.
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    // Shift's starting time in epoch format
    @NonNull
    private Long startTime;

    // Shift's ending time in epoch format.
    @NonNull
    private Long endTime;

    // Room constructor used to create new shifts
    public Shift(@NonNull Long startTime, @NonNull Long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Custom constructor, flagged to be ignored by the Room parser,
    // used to update shifts by specifying their id.
    @Ignore
    public Shift(Integer id, @NonNull Long startTime, @NonNull Long endTime) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and setters

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
