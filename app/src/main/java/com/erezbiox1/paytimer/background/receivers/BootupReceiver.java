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

    /**
     * Called when the device boot ups.
     * @param context context to be used by the method
     * @param intent the intent that called this receiver.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Log it to the console
        Log.d("BootupReceiver", "Booting up, adding geofence.");

        // Tell the LocationController to re-add the geofence ( it is deleted on restart )
        new LocationController(context).addGeofence(true);

        // Tell the TimeNotificationsController to re-add it's notification ( it is deleted on restart )
        TimeNotificationsController.rescheduleNotification(context);
    }

}
