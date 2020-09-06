/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.erezbiox1.paytimer.background.receivers.GeofenceReceiver;
import com.erezbiox1.paytimer.background.receivers.TimeNotificationsReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeNotificationsController {

    /**
     * Reschedule the time notification reminder
     * @param context provided context
     */
    public static void rescheduleNotification(Context context){
        // Get the default preferences and alarm manager from the context
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Create a pending broadcast intent that is aimed at TimeNotificationReceiver.
        // This broadcast will be received by the time notification receiver, that will
        // evaluate the conditions and will send out the time reminder if the conditions apply
        // ( if it's in the right day, and the time notification are enabled )

        Intent intent = new Intent(context, TimeNotificationsReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // If the time notifications are enabled, ( this method is called in every change )
        // Schedule the broadcast. ( that will in-turn send-out the notification )
        if(sharedPreferences.getBoolean("time_notifications_enabled", false)){
            // Get the broadcast triggering time ( the soon as possible hour:minute defined by the time prefs )
            long triggerTime = getTime(sharedPreferences);

            // Set the repeating broadcast. Trigger it every day and each day
            // check inside the receiver if that day is enabled in the prefs.
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        } else {
            // If the time notifications aren't enabled, cancel the broadcast pending intent.
            alarmManager.cancel(pendingIntent);
        }
    }

    /**
     * Get the broadcast triggering time ( the soon as possible hour:minute defined by the time prefs )
     * @param sharedPreferences time preferences
     * @return the broadcast triggering time
     */
    private static long getTime(SharedPreferences sharedPreferences){
        // Set the current time as the default time. ( store it in a string because this
        // is how it's stored in the prefs, because only one value can be persisted ).
        String defaultTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(System.currentTimeMillis());

        // Get the stored time in the prefs, default to the stored default time
        String formatted = sharedPreferences.getString("time_notifications", defaultTime);

        // Parse the hour and minute format
        String[] split = formatted.split(":");
        int hour = Integer.parseInt(split[0]);
        int minute = Integer.parseInt(split[1]);

        // Get the current time, set the prefs provided hour and minute
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        // Set it to trigger in the first millisecond of the minute.
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If the provided time has already passed today, schedule the recurring notification starting tomorrow
        // Do this by checking if the current time if after the scheduled time, if so add a single day to the time.
        if (calendar.getTimeInMillis() <= System.currentTimeMillis())
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);

        // Return the final trigger time.
        return calendar.getTimeInMillis();
    }

}
