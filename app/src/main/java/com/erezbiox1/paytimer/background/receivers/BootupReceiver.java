/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.background.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.erezbiox1.paytimer.utils.LocationController;
import com.erezbiox1.paytimer.utils.TimeNotificationsController;

public class BootupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootupReceiver", "Booting up, adding geofence.");
        new LocationController(context).addGeofence(true);
        TimeNotificationsController.rescheduleNotification(context);
    }

}
