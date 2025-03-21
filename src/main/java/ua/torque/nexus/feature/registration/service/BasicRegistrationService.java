package ua.torque.nexus.feature.registration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.torque.nexus.feature.registration.model.User;
import ua.torque.nexus.feature.registration.model.dto.RegistrationRequest;
import ua.torque.nexus.feature.registration.model.dto.RegistrationResponse;
import ua.torque.nexus.feature.registration.model.mapper.RegistrationMapper;
import ua.torque.nexus.feature.registration.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class BasicRegistrationService implements RegistrationService {
    private final UserRepository userRepository;
    private final RegistrationMapper registrationMapper;

    @Override
    public RegistrationResponse registerUser(RegistrationRequest request) {
        return null;
    }

    @Override
    public User requestToUser(RegistrationRequest request) {
        return registrationMapper.toUser(request)
                .orElseThrow(() -> new RuntimeException("Failed to convert RegistrationRequest to User."));
    }


    @Override
    public RegistrationResponse userToRegistrationResponse(User user) {
        return registrationMapper.toUserRegistrationResponse(user)
                .orElseThrow(() -> new RuntimeException("Failed to convert User to RegistrationResponse."));
    }

    @Override
    public boolean userIsRegistered(User user) {
        return userRepository.findByEmail(user.getEmail()).isPresent();
    }
}
