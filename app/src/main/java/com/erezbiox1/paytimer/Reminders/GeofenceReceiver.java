/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.Reminders;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.erezbiox1.paytimer.MainActivity;
import com.erezbiox1.paytimer.R;

public class GeofenceReceiver extends BroadcastReceiver {

    public static final String NOTIFICATION_CHANNEL_ID = "28764";
    private final static String default_notification_channel_id = "default";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "GEOOO", Toast.LENGTH_SHORT).show();

        ReminderController.notify(context);
    }
}
