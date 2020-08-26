/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.Reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.erezbiox1.paytimer.MainActivity;

import static com.erezbiox1.paytimer.MainActivity.START_TIME_PREF;

public class StartActionReceiver extends BroadcastReceiver {

    /*
    This receiver both updates the current starting time saved ( and by that starting the shift ),
    and also updates the UI using the MainActivity observer ( NotificationStartActionObserver ).
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the shared preferences
        SharedPreferences pref = context.getSharedPreferences("TimePref", 0);

        // If the shift is already running ( there is a saved starting time ) skip starting the shift.
        if(!pref.contains(START_TIME_PREF)) {

            // Get the current time
            long currentTime = pref.getLong(START_TIME_PREF, System.currentTimeMillis());

            // Save it as the starting time
            SharedPreferences.Editor editor = pref.edit();
            editor.putLong(START_TIME_PREF, currentTime);
            editor.apply();

            // Update the activity
            MainActivity.NotificationStartActionObserver.getInstance().execute();

        }

        // Cancel the notification
        ReminderController.cancel(context);
    }
}
