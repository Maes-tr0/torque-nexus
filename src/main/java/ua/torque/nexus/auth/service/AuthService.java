package ua.torque.nexus.auth.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.torque.nexus.auth.dto.RegistrationRequest;
import ua.torque.nexus.auth.dto.RegistrationResponse;
import ua.torque.nexus.auth.dto.ResetPasswordRequest;
import ua.torque.nexus.auth.dto.ResetPasswordResponse;
import ua.torque.nexus.auth.mapper.AuthMapper;
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

        userDataService.saveNewUser(user);
        log.info("User saved with email: {}", user.getEmail());

        RegistrationResponse response = authMapper.userToRegistrationResponse(user);
        log.info("Registration response generated for email: {}", user.getEmail());

        return response;
    }

    public ResetPasswordResponse resetPassword(@Valid ResetPasswordRequest request) {
        log.info("Processing reset password for email: {}", request.getEmail());

        User user = authMapper.resetPasswordRequestToUser(request);

        userDataService.updatePasswordUser(user, user.getPassword());
        log.info("Password updated for user with email: {}", user.getEmail());

        ResetPasswordResponse response = authMapper.userToResetPasswordResponse(user);
        log.info("ResetPassword response generated for email: {}", user.getEmail());

        return response;
    }
}
