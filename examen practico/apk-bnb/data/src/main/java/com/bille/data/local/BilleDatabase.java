package com.bille.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RegistrationEntity.class}, version = 1, exportSchema = false)
public abstract class BilleDatabase extends RoomDatabase {

    public abstract RegistrationDao registrationDao();
}
