/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.erezbiox1.paytimer.Reminders.LocationHandler;

public class BootupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new LocationHandler(context).addGeofence();
    }

}
