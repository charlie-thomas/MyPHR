package com.csbgroup.myphr.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {AppointmentsEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract AppointmentsDao appointmentsDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "database").build();
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
