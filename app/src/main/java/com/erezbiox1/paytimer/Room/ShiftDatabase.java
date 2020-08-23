/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Shift.class}, version = 1)
public abstract class ShiftDatabase extends RoomDatabase {

    // The shift's database data access object (DAO)
    public abstract ShiftDao shiftDao();

    // The singleton instance
    private static ShiftDatabase INSTANCE;

    /**
     * Get the singleton database, will use the application context provided to
     * initiate the said database.
     * @param context application context that will be used to lazy init the database
     * @return the database instance.
     */
    public static ShiftDatabase getDatabase(final Context context) {
        // If the instance is null ( no database was initiated yet ), create a new one
        if (INSTANCE == null) {
            // Get a static lock on the class
            synchronized (ShiftDatabase.class) {
                // check again if the instance is null and have not been changed
                // by another thread in the meantime
                if (INSTANCE == null) {
                    // Create the Room database,
                    // TODO change "fallback to destructive migration"
                    // this is here for the development and should not be used in production.
                    INSTANCE = Room
                            .databaseBuilder(context.getApplicationContext(),
                            ShiftDatabase.class, "shift_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        // return the instance
        return INSTANCE;
    }
}
