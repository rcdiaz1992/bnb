package com.bille.data.local;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface RegistrationDao {

    @Insert
    long insert(RegistrationEntity registration);
}
