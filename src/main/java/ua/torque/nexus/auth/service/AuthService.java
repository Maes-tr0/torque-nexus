package ua.torque.nexus.auth.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.torque.nexus.auth.dto.request.LoginRequest;
import ua.torque.nexus.auth.dto.request.RegistrationRequest;
import ua.torque.nexus.auth.dto.request.ResetPasswordRequest;
import ua.torque.nexus.auth.dto.response.AuthResponse;
import ua.torque.nexus.auth.dto.response.ResetPasswordResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RegistrationService registrationService;
    private final LoginService loginService;
    private final PasswordResetService passwordResetService;

    public AuthResponse register(@Valid RegistrationRequest req) {
        log.debug("Delegating registration for {}", req.getEmail());
        return registrationService.registerUser(req);
    }

    public AuthResponse login(@Valid LoginRequest req) {
        log.debug("Delegating login for {}", req.getEmail());
        return loginService.loginUser(req);
    }

    public ResetPasswordResponse resetPassword(@Valid ResetPasswordRequest req) {
        log.debug("Delegating password-reset for {}", req.getEmail());
        return passwordResetService.resetUserPassword(req);
    }
}
