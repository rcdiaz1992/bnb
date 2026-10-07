package com.bille.app.di;

import com.bille.domain.registration.ContinueRegistrationUseCase;
import com.bille.domain.registration.RegistrationRepository;
import com.bille.domain.validation.RegistrationValidator;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public final class DomainModule {

    @Provides
    public static RegistrationValidator provideRegistrationValidator() {
        return new RegistrationValidator();
    }

    @Provides
    public static ContinueRegistrationUseCase provideContinueRegistrationUseCase(
            RegistrationValidator validator,
            RegistrationRepository repository
    ) {
        return new ContinueRegistrationUseCase(validator, repository);
    }
}
