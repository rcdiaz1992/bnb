package com.bille.data.di;

import android.content.Context;

import androidx.room.Room;

import com.bille.data.local.BilleDatabase;
import com.bille.data.local.RegistrationDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public final class DatabaseModule {

    @Provides
    @Singleton
    public static BilleDatabase provideDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, BilleDatabase.class, "bille.db").build();
    }

    @Provides
    public static RegistrationDao provideRegistrationDao(BilleDatabase database) {
        return database.registrationDao();
    }
}
