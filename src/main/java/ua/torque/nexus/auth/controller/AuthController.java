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
        log.info("event=request_received httpMethod=POST path=/api/v1/auth/register email={}",
                request.getEmail());

        AuthResponse response = authService.register(request);

        log.info("event=request_completed httpMethod=POST path=/api/v1/auth/register status=success httpStatus=201 email={}",
                response.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        log.info("event=request_received httpMethod=POST path=/api/v1/auth/reset-password email={}",
                request.getEmail());

        ResetPasswordResponse response = authService.resetPassword(request);

        log.info("event=request_completed httpMethod=POST path=/api/v1/auth/reset-password status=success httpStatus=202 email={}",
                response.email());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info("event=request_received httpMethod=POST path=/api/v1/auth/login email={}",
                request.getEmail());

        AuthResponse response = authService.login(request);

        log.info("event=request_completed httpMethod=POST path=/api/v1/auth/login status=success httpStatus=202 email={}",
                response.email());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}