/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.background.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.erezbiox1.paytimer.utils.ReminderController;

public class GeofenceReceiver extends BroadcastReceiver {

    /**
     * Called when the used enters a specified geo-fence.
     * @param context provided context
     * @param intent provided intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Tell the reminder controller to send the user a notification reminder.
        ReminderController.notify(context);
    }
}
