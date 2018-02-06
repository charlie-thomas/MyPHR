package com.csbgroup.myphr.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {AppointmentsEntity.class, MedicineEntity.class,
        StatisticsEntity.class, ContactsEntity.class, InvestigationsEntity.class}, version = 7)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract AppointmentsDao appointmentsDao();
    public abstract MedicineDao medicineDao();
    public abstract ContactsDao contactsDao();
    public abstract StatisticsDao statisticsDao();
    public abstract InvestigationsDao investigationDao();

    public static AppDatabase getAppDatabase(Context context) {

        if (INSTANCE == null) INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "database").fallbackToDestructiveMigration().build();
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
