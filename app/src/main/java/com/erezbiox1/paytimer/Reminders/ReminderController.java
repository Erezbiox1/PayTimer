/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.Reminders;

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

import com.erezbiox1.paytimer.BuildConfig;
import com.erezbiox1.paytimer.MainActivity;
import com.erezbiox1.paytimer.R;

public class ReminderController {

    public static final String NOTIFICATION_START_ACTION = BuildConfig.APPLICATION_ID + ".action.START";
    private static final String REMINDER_CHANNEL_ID = "reminder_channel_id";
    private static final int NOTIFICATION_ID = 199;

    public static void notify(Context context){
        NotificationManager manager;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), REMINDER_CHANNEL_ID);
        PendingIntent mainActivityIntent = PendingIntent.getActivity(
                context, 0, new Intent(context.getApplicationContext(), MainActivity.class), 0);

        PendingIntent startShiftIntent = PendingIntent.getBroadcast(
                context, 1, new Intent(context.getApplicationContext(), StartActionReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.setBigContentTitle(context.getString(R.string.app_name));
        bigText.setSummaryText("Default job");

        builder.setContentIntent(mainActivityIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentTitle("Start your shift!");
        builder.setContentText("This is your daily reminded to start your shift!");
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_time, "Start", startShiftIntent));
        builder.setAutoCancel(true);
        builder.setStyle(bigText);

        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(
                    REMINDER_CHANNEL_ID,
                    "Shift Reminders",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.YELLOW);
            channel.enableVibration(true);
            channel.setDescription("Shift reminders");

            manager.createNotificationChannel(channel);
            builder.setChannelId(REMINDER_CHANNEL_ID);
        }

        manager.notify(NOTIFICATION_ID, builder.build());
    }

    public static void cancel(Context context){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
    }
}
