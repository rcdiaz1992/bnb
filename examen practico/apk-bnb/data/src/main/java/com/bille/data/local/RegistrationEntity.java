package com.bille.data.local;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "registrations")
public class RegistrationEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String phone = "";

    @NonNull
    public String carnet = "";

    public boolean hasComplement;

    @NonNull
    public String complement = "";

    @Nullable
    public Double latitude;

    @Nullable
    public Double longitude;

    public long createdAtEpochMillis;
}
