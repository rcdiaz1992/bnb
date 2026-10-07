package com.bille.data.di;

import com.bille.data.location.AndroidLocationGateway;
import com.bille.data.registration.OnboardingService;
import com.bille.data.registration.OnboardingServiceImpl;
import com.bille.data.registration.RegistrationRepositoryImpl;
import com.bille.domain.location.LocationGateway;
import com.bille.domain.registration.RegistrationRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class DataModule {

    @Binds
    @Singleton
    public abstract LocationGateway bindLocationGateway(AndroidLocationGateway implementation);

    @Binds
    @Singleton
    public abstract OnboardingService bindOnboardingService(OnboardingServiceImpl implementation);

    @Binds
    @Singleton
    public abstract RegistrationRepository bindRegistrationRepository(RegistrationRepositoryImpl implementation);
}
