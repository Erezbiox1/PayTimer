/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
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

    @NonNull
    private Double hourlyRate;

    // The given tip ( will be added to the total salary )
    private Integer tip;

    // Default constructor required for calls to DataSnapshot.getValue(Shift.class)
    public Shift(){ }

    // Room constructor used to create new shifts
    public Shift(@NonNull Long startTime, @NonNull Long endTime, @NonNull Double hourlyRate, Integer tip) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.hourlyRate = hourlyRate;
        this.tip = tip;
    }

    // Custom constructor, flagged to be ignored by the Room parser,
    // used to update shifts by specifying their id.
    @Ignore
    public Shift(Integer id, @NonNull Long startTime, @NonNull Long endTime, @NonNull Double hourlyRate, Integer tip) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hourlyRate = hourlyRate;
        this.tip = tip;
    }

    // Calculated getters

    public long getTotalHours(){
        return getEndTime() - getStartTime();
    }

    public double getTotalPay(){
        return ((double) getTotalHours() / 1000 / 60 / 60) * getHourlyRate() + (tip != null ? tip : 0);
    }

    // Getters and setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @NonNull
    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(@NonNull Long startTime) {
        this.startTime = startTime;
    }

    @NonNull
    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(@NonNull Long endTime) {
        this.endTime = endTime;
    }

    public Integer getTip() {
        return tip;
    }

    public void setTip(Integer tip) {
        this.tip = tip;
    }

    @NonNull
    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(@NonNull Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    @Exclude
    public Map<String, Object> toMap(){
        Map<String, Object> result = new HashMap<>();

        result.put("startTime", startTime);
        result.put("endTime", endTime);
        result.put("hourlyRate", hourlyRate);
        result.put("tip", tip);

        return result;
    }
}
