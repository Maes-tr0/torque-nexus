package ua.torque.nexus.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.torque.nexus.auth.dto.request.LoginRequest;
import ua.torque.nexus.auth.dto.request.RegistrationRequest;
import ua.torque.nexus.auth.dto.request.ResetPasswordRequest;
import ua.torque.nexus.auth.dto.response.AuthResponse;
import ua.torque.nexus.auth.dto.response.ResetPasswordResponse;
import ua.torque.nexus.auth.service.AuthService;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegistrationRequest request) {
        log.info("POST /register for email={}", request.getEmail());

        AuthResponse resp = authService.register(request);
        log.info("<-Registration-> completed for email={}", resp.email());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resp);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        log.info("POST /reset-password for email={}", request.getEmail());

        ResetPasswordResponse resp = authService.resetPassword(request);
        log.info("<-Password-reset> completed for email={}", resp.email());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info("POST /login for email={}", request.getEmail());

        AuthResponse resp = authService.login(request);
        log.info("<-Login-> completed for email={}", resp.email());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(resp);
    }
}
