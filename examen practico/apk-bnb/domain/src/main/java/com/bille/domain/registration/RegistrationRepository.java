package com.bille.domain.registration;

import com.bille.domain.model.RegistrationForm;
import com.bille.domain.model.RegistrationReceipt;

public interface RegistrationRepository {
    RegistrationReceipt register(RegistrationForm form);
}
