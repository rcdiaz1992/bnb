package com.bille.data.registration;

import com.bille.domain.location.LocationFix;
import com.bille.domain.model.RegistrationForm;
import com.bille.domain.model.RegistrationReceipt;

public interface OnboardingService {
    RegistrationReceipt consume(RegistrationForm form, LocationFix location);
}
