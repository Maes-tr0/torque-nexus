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
import ua.torque.nexus.auth.mapper.AuthMapper;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AuthMapper authMapper;

    public AuthResponse registerUser(@Valid RegistrationRequest request) {
        log.info("Registration request received for email: {}", request.getEmail());

        User user = authMapper.registrationRequestToUser(request);
        log.debug("Mapped registration request to User: {}", user);

        String token = userService.saveNewUser(user);
        log.info("User {} registered successfully. Token generated: {}", user.getEmail(), token);

        AuthResponse response = AuthResponse.builder()
                .email(user.getEmail())
                .token(token)
                .message("Registration successful — please confirm your email")
                .build();
        log.info("Registration response created for email: {}", user.getEmail());

        return response;
    }

    public ResetPasswordResponse resetPassword(@Valid ResetPasswordRequest request) {
        log.info("Reset password request received for email: {}", request.getEmail());

        User userByEmail = userService.getUserByEmail(request.getEmail());
        log.debug("User retrieved for reset password: {}", userByEmail);

        userService.updatePasswordUser(userByEmail, request.getNewPassword());
        log.info("Password reset successfully for email: {}", request.getEmail());

        ResetPasswordResponse response = ResetPasswordResponse.builder()
                .email(userByEmail.getEmail())
                .message("Reset password successful")
                .build();
        log.info("Reset password response created for email: {}", userByEmail.getEmail());

        return response;
    }

    public AuthResponse login(@Valid LoginRequest request) {
        log.info("Processing login for user: {}", request.getEmail());

        String token = userService.loginUser(request.getEmail(), request.getPassword());

        log.info("Successfully generated JWT token for user: {}", request.getEmail());

        return AuthResponse.builder()
                .email(request.getEmail())
                .token(token)
                .message("Successfully generated JWT token for user")
                .build();
    }
}
