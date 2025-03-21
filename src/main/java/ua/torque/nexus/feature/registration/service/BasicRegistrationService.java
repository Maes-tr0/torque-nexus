package ua.torque.nexus.feature.registration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.torque.nexus.feature.registration.model.User;
import ua.torque.nexus.feature.registration.model.dto.RegistrationRequest;
import ua.torque.nexus.feature.registration.model.dto.RegistrationResponse;
import ua.torque.nexus.feature.registration.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasicRegistrationService implements RegistrationService {
    private final UserRepository userRepository;

    @Override
    public RegistrationResponse registerUser(RegistrationRequest request) {
        return null;
    }

    @Override
    public Optional<User> requestToUser(RegistrationRequest request) {
        return Optional.empty();
    }

    @Override
    public Optional<RegistrationResponse> userToRegistrationResponse(User user) {
        return Optional.empty();
    }

    @Override
    public boolean userIsRegistered(User user) {
        return false;
    }

    @Override
    public boolean userIsValid(User user) {
        return false;
    }
}
