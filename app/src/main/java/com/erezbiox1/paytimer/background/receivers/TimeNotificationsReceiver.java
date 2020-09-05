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

    /**
     * Called when the time notification is triggered ( at the specified time )
     * @param context provided context
     * @param intent provided intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the default shared shared preferences from the provided context.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // If the time notifications is enabled ( default to false ) and the notification is today,
        // ( it is called every day at the specified time due to android api restrictions )
        if(prefs.getBoolean("time_notifications_enabled", false) && isToday(prefs))
            // Tell the reminder controller to send the user a notification reminder.
            ReminderController.notify(context);
    }

    /**
     * Checks if the current system time is at match with the allowed days in the preference.
     * @param prefs
     * @return
     */
    private static boolean isToday(SharedPreferences prefs){
        // Get the current weekday.
        int weekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        // Switch based on that day, each case check if that day is enabled in the preference. default to false.
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
