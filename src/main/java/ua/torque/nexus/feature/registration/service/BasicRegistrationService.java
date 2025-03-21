package ua.torque.nexus.feature.registration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.torque.nexus.feature.registration.model.User;
import ua.torque.nexus.feature.registration.model.dto.RegistrationRequest;
import ua.torque.nexus.feature.registration.model.dto.RegistrationResponse;
import ua.torque.nexus.feature.registration.model.mapper.RegistrationMapper;

@Service
@RequiredArgsConstructor
public class BasicRegistrationService implements RegistrationService {
    private final UserDataService userDataService;
    private final RegistrationMapper registrationMapper;


    @Override
    public RegistrationResponse registerUser(RegistrationRequest request) {
        final User user = requestToUser(request);

        userDataService.save(user);

        return userToRegistrationResponse(user);
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
}
