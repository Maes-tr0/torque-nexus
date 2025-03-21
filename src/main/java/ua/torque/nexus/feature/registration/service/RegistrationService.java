package ua.torque.nexus.feature.registration.service;

import ua.torque.nexus.feature.registration.model.User;
import ua.torque.nexus.feature.registration.model.dto.RegistrationRequest;
import ua.torque.nexus.feature.registration.model.dto.RegistrationResponse;

public interface RegistrationService {
    RegistrationResponse registerUser(RegistrationRequest request);

    User requestToUser(RegistrationRequest request);

    RegistrationResponse userToRegistrationResponse(User user);

    boolean userIsRegistered(User user);
}
