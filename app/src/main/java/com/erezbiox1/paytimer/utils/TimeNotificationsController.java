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

    public static void rescheduleNotification(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, TimeNotificationsReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(sharedPreferences.getBoolean("time_notifications_enabled", false)){
            long triggerTime = getTime(sharedPreferences);

            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }

    private static long getTime(SharedPreferences sharedPreferences){
        String defaultTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(System.currentTimeMillis());
        String formatted = sharedPreferences.getString("time_notifications", defaultTime);

        String[] split = formatted.split(":");
        int hour = Integer.parseInt(split[0]);
        int minute = Integer.parseInt(split[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis())
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);

        return calendar.getTimeInMillis();
    }

}
