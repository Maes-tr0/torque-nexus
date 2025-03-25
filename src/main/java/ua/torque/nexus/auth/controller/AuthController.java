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
import ua.torque.nexus.auth.dto.RegistrationRequest;
import ua.torque.nexus.auth.dto.RegistrationResponse;
import ua.torque.nexus.auth.dto.ResetPasswordRequest;
import ua.torque.nexus.auth.dto.ResetPasswordResponse;
import ua.torque.nexus.auth.service.AuthService;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@RequestBody @Valid RegistrationRequest request) {
        log.info("Received registration request for email: {}", request.getEmail());

        RegistrationResponse response = authService.registerUser(request);

        log.info("User registered successfully with email: {}", response.email());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Received reset password request for email: {}", request.getEmail());

        ResetPasswordResponse response = authService.resetPassword(request);

        log.info("Password reset process initiated successfully for email: {}", response.email());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(response);
    }
}
