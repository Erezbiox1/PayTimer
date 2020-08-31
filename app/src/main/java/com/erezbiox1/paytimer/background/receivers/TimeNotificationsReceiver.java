/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.background.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.erezbiox1.paytimer.utils.ReminderController;

import java.util.Calendar;

public class TimeNotificationsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(prefs.getBoolean("time_notifications_enabled", false) && isToday(prefs))
            ReminderController.notify(context);
    }

    private static boolean isToday(SharedPreferences prefs){
        int weekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch(weekday){
            case Calendar.SUNDAY: return prefs.getBoolean("time_notifications_day_" + "sunday", false);
            case Calendar.MONDAY: return prefs.getBoolean("time_notifications_day_" + "monday", false);
            case Calendar.TUESDAY: return prefs.getBoolean("time_notifications_day_" + "tuesday", false);
            case Calendar.WEDNESDAY: return prefs.getBoolean("time_notifications_day_" + "wednesday", false);
            case Calendar.THURSDAY: return prefs.getBoolean("time_notifications_day_" + "thursday", false);
            case Calendar.FRIDAY: return prefs.getBoolean("time_notifications_day_" + "friday", false);
            case Calendar.SATURDAY: return prefs.getBoolean("time_notifications_day_" + "saturday", false);
            default: return false;
        }
    }
}
