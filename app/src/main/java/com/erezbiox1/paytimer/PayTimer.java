/*
 * Copyright (c) 2021. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class PayTimer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable firebase realtime database offline data persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
