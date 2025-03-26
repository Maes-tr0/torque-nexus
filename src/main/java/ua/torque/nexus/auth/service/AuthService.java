package ua.torque.nexus.auth.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.torque.nexus.auth.dto.request.LoginRequest;
import ua.torque.nexus.auth.dto.request.RegistrationRequest;
import ua.torque.nexus.auth.dto.response.LoginResponse;
import ua.torque.nexus.auth.dto.response.RegistrationResponse;
import ua.torque.nexus.auth.dto.request.ResetPasswordRequest;
import ua.torque.nexus.auth.dto.response.ResetPasswordResponse;
import ua.torque.nexus.auth.mapper.AuthMapper;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.service.UserDataService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDataService userDataService;
    private final AuthMapper authMapper;

    public RegistrationResponse registerUser(@Valid RegistrationRequest request) {
        log.info("Processing registration for email: {}", request.getEmail());

        User user = authMapper.registrationRequestToUser(request);

        ConfirmationToken token = userDataService.saveNewUser(user);
        log.info("User saved with email: {}", user.getEmail());

        RegistrationResponse base = authMapper.toRegistrationResponse(user);
        RegistrationResponse response = RegistrationResponse.builder()
                .email(base.email())
                .message(base.message())
                .token(token.getToken())
                .build();
        log.info("Registration response generated for email: {}", user.getEmail());

        return response;
    }

    public ResetPasswordResponse resetPassword(@Valid ResetPasswordRequest request) {
        log.info("Processing reset password for email: {}", request.getEmail());

        User user = userDataService.getUserByEmail(request.getEmail());

        userDataService.updatePasswordUser(user, request.getNewPassword());
        log.info("Password updated for user with email: {}", user.getEmail());

        ResetPasswordResponse response = authMapper.userToResetPasswordResponse(user);
        log.info("ResetPassword response generated for email: {}", user.getEmail());

        return response;
    }

    public LoginResponse login(@Valid LoginRequest request) {
        log.info("Processing login for user: {}", request.getEmail());

        String token = userDataService.loginUser(request.getEmail(), request.getPassword());

        log.info("Successfully generated JWT token for user: {}", request.getEmail());

        return LoginResponse.builder()
                .email(request.getEmail())
                .token(token)
                .message("Successfully generated JWT token for user")
                .build();
    }
}
