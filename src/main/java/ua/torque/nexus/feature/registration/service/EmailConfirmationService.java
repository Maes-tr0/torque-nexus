package ua.torque.nexus.feature.registration.service;

import ua.torque.nexus.feature.token.email.model.dto.ConfirmationResponse;

public interface EmailConfirmationService {
    ConfirmationResponse confirmEmail(String token);
}
