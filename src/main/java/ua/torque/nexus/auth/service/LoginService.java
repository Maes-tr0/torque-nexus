package ua.torque.nexus.auth.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.torque.nexus.auth.dto.request.LoginRequest;
import ua.torque.nexus.auth.dto.response.AuthResponse;
import ua.torque.nexus.auth.mapper.AuthResponseFactory;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.service.UserService;


@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserService userService;

    public AuthResponse loginUser(@Valid LoginRequest request) {
        log.info("Login start for email={}", request.getEmail());

        User user = userService.getUserByEmail(request.getEmail());
        log.debug("User fetched: email={}, confirmed={}, role={}",
                user.getEmail(), user.isEmailConfirmed(), user.getRole().getType());

        String jwt = userService.loginUser(user, request.getPassword());
        log.info("JWT token generated for email={}", user.getEmail());

        return AuthResponseFactory.buildAuthResponse(user, jwt);
    }
}
