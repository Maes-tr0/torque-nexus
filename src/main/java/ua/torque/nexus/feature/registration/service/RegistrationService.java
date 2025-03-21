package ua.torque.nexus.feature.registration.service;

import ua.torque.nexus.feature.registration.model.User;
import ua.torque.nexus.feature.registration.model.dto.RegistrationRequest;
import ua.torque.nexus.feature.registration.model.dto.RegistrationResponse;

import java.util.Optional;

public interface RegistrationService {
    RegistrationResponse registerUser(RegistrationRequest request);

    Optional<User> requestToUser(RegistrationRequest request);

    Optional<RegistrationResponse> userToRegistrationResponse(User user);

    boolean userIsRegistered(User user);

    boolean userIsValid(User user);
}
