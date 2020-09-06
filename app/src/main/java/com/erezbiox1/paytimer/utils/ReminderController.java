/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;

import com.erezbiox1.paytimer.background.receivers.StartActionReceiver;
import com.erezbiox1.paytimer.activities.MainActivity;
import com.erezbiox1.paytimer.R;

import java.util.Calendar;

import static com.erezbiox1.paytimer.activities.MainActivity.START_TIME_PREF;
import static com.erezbiox1.paytimer.activities.MainActivity.TIME_PREF;

public class ReminderController {

    // Notification constants
    private static final String REMINDER_CHANNEL_ID = "reminder_channel_id";
    private static final String NOTIFIED_TIME_PREF = "NOTIFIED_TIME_PREF";
    private static final int NOTIFICATION_ID = 199;

    /**
     * Create the shift reminder notification.
     * @param context provided context
     */
    public static void notify(Context context){
        // Check if the conditions apply ( time notifications are enabled, current day is enabled.. )
        if(!checkConditions(context))
            return;

        // Get the notification manager from the provided context
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), REMINDER_CHANNEL_ID);
        PendingIntent mainActivityIntent = PendingIntent.getActivity(
                context, 0, new Intent(context.getApplicationContext(), MainActivity.class), 0);

        // The "Start" action intent
        PendingIntent startShiftIntent = PendingIntent.getBroadcast(
                context, 1, new Intent(context.getApplicationContext(), StartActionReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

        // Setting the builder values. ( aka building the notification. )
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.setBigContentTitle(context.getString(R.string.app_name));
        //bigText.setSummaryText("Default job");

        builder.setContentIntent(mainActivityIntent);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle(context.getString(R.string.start_your_shift_notification));
        builder.setContentText(context.getString(R.string.start_your_shift_text_notification));
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_time, context.getString(R.string.start_notification_action), startShiftIntent));
        builder.setAutoCancel(true);
        builder.setStyle(bigText);

        // If it's android oreo or above, create a notification channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    REMINDER_CHANNEL_ID,
                    context.getString(R.string.shift_reminder_notification),
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.YELLOW);
            channel.enableVibration(true);
            channel.setDescription(context.getString(R.string.shift_notification_reminders));

            manager.createNotificationChannel(channel);
            builder.setChannelId(REMINDER_CHANNEL_ID);
        }

        // Save now as the last notification time
        SharedPreferences pref = context.getSharedPreferences(TIME_PREF, 0);
        pref.edit().putLong(NOTIFIED_TIME_PREF, System.currentTimeMillis()).apply();

        // Notify the user.
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Checks if the notifications can be sent
     * @param context provided context
     * @return if the notifications can be sent
     */
    private static boolean checkConditions(Context context){
        // Check if the user was already notified today ( and if that is allowed )
        SharedPreferences pref = context.getSharedPreferences(TIME_PREF, 0);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        if(settings.getBoolean("notify_once_a_day", true) && pref.contains(NOTIFIED_TIME_PREF) && isToday(pref.getLong(NOTIFIED_TIME_PREF, 0)))
            return false;

        // Check if the shift is already running
        if(pref.contains(START_TIME_PREF))
            return false;

        // Otherwise return true
        return true;
    }

    /**
     * Check if the provided time is today
     * @param time provided time
     * @return is that time today
     */
    private static boolean isToday(long time){
        // Current time calender
        Calendar nowC = Calendar.getInstance();
        nowC.setTimeInMillis(System.currentTimeMillis());

        // Provided time calender
        Calendar timeC = Calendar.getInstance();
        timeC.setTimeInMillis(time);

        // Check if the are in the same day ( DAY_OF_YEAR )
        return nowC.get(Calendar.DAY_OF_YEAR) == timeC.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Cancel the notification ( in the event of the "start" action button clicked )
     * @param context provided context
     */
    public static void cancel(Context context){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
    }
}
