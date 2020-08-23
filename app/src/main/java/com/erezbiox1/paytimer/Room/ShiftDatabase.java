package com.erezbiox1.paytimer.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Shift.class}, version = 1)
public abstract class ShiftDatabase extends RoomDatabase {

    public abstract ShiftDao shiftDao();

    private static ShiftDatabase INSTANCE;

    public static ShiftDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ShiftDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(context.getApplicationContext(),
                            ShiftDatabase.class, "shift_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
