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

    public AuthResponse register(@Valid RegistrationRequest request) {
        log.debug("Delegating registration for {}", request.getEmail());
        return registrationService.registerUser(request);
    }

    public AuthResponse login(@Valid LoginRequest request) {
        log.debug("Delegating login for {}", request.getEmail());
        return loginService.loginUser(request);
    }

    public ResetPasswordResponse resetPassword(@Valid ResetPasswordRequest request) {
        log.debug("Delegating password-reset for {}", request.getEmail());
        return passwordResetService.resetUserPassword(request);
    }
}
