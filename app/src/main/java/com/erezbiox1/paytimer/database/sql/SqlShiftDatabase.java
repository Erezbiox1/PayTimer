/*
 * Copyright (c) 2021. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.database.sql;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.erezbiox1.paytimer.model.Shift;

@Database(entities = {Shift.class}, version = 4)
public abstract class SqlShiftDatabase extends RoomDatabase {

    // The shift's database data access object (DAO)
    public abstract SqlShiftDao shiftDao();

    // The singleton instance
    private static SqlShiftDatabase INSTANCE;

    /**
     * Get the singleton database, will use the application context provided to
     * initiate the said database.
     * @param context application context that will be used to lazy init the database
     * @return the database instance.
     */
    public static SqlShiftDatabase getDatabase(final Context context) {
        // If the instance is null ( no database was initiated yet ), create a new one
        if (INSTANCE == null) {
            // Get a static lock on the class
            synchronized (SqlShiftDatabase.class) {
                // check again if the instance is null and have not been changed
                // by another thread in the meantime
                if (INSTANCE == null) {
                    // Create the Room database,
                    // TODO change "fallback to destructive migration"
                    // this is here for the development and should not be used in production.
                    INSTANCE = Room
                            .databaseBuilder(context.getApplicationContext(),
                            SqlShiftDatabase.class, "shift_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        // return the instance
        return INSTANCE;
    }
}
