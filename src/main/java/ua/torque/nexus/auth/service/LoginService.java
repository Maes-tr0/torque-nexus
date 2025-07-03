package ua.torque.nexus.auth.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.torque.nexus.auth.dto.request.LoginRequest;
import ua.torque.nexus.auth.dto.response.AuthResponse;
import ua.torque.nexus.auth.mapper.AuthResponseFactory;
import ua.torque.nexus.user.service.AuthenticationService;


@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationService authenticationService;


    public AuthResponse loginUser(@Valid LoginRequest request) {
        log.info("event=login_started email={}", request.getEmail());

        String jwt = authenticationService.loginUser(request.getEmail(), request.getPassword());

        log.info("event=login_finished status=success email={}", request.getEmail());
        return AuthResponseFactory.buildAuthResponse(request.getEmail(), jwt);
    }
}