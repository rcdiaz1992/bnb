package com.bille.data.registration;

import com.bille.data.local.RegistrationDao;
import com.bille.data.local.RegistrationEntity;
import com.bille.domain.location.LocationFix;
import com.bille.domain.model.RegistrationForm;
import com.bille.domain.model.RegistrationReceipt;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class OnboardingServiceImpl implements OnboardingService {

    private final RegistrationDao registrationDao;

    @Inject
    public OnboardingServiceImpl(RegistrationDao registrationDao) {
        this.registrationDao = registrationDao;
    }

    @Override
    public RegistrationReceipt consume(RegistrationForm form, LocationFix location) {
        RegistrationEntity entity = new RegistrationEntity();
        entity.phone = form.phone();
        entity.carnet = form.carnet();
        entity.hasComplement = form.hasComplement();
        entity.complement = form.complement();
        entity.latitude = location.available() ? location.latitude() : null;
        entity.longitude = location.available() ? location.longitude() : null;
        entity.createdAtEpochMillis = System.currentTimeMillis();
        long id = registrationDao.insert(entity);
        return new RegistrationReceipt(Long.toString(id));
    }
}
