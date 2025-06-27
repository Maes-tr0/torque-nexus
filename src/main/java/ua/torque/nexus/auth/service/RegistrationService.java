package ua.torque.nexus.auth.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.torque.nexus.auth.dto.request.RegistrationRequest;
import ua.torque.nexus.auth.dto.response.AuthResponse;
import ua.torque.nexus.auth.mapper.AuthMapper;
import ua.torque.nexus.auth.mapper.AuthResponseFactory;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private final AuthMapper authMapper;

    public AuthResponse registerUser(@Valid RegistrationRequest request) {
        log.info("Registration start for email={}", request.getEmail());

        User user = authMapper.toUser(request);
        log.debug("Mapped DTO → User for email={}", user.getEmail());

        String confirmationToken = userService.createUser(user);
        log.info("Confirmation token issued for email={}", user.getEmail());

        return AuthResponseFactory.buildAuthResponse(user, confirmationToken);
    }
}
