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
        log.info("Registration request received for email: {}", request.getEmail());

        User user = authMapper.registrationRequestToUser(request);
        log.debug("Mapped registration request to User: {}", user);

        String token = userDataService.saveNewUser(user);
        log.info("User {} registered successfully. Token generated: {}", user.getEmail(), token);

        RegistrationResponse response = RegistrationResponse.builder()
                .email(user.getEmail())
                .token(token)
                .message("Registration successful — please confirm your email")
                .build();
        log.info("Registration response created for email: {}", user.getEmail());

        return response;
    }

    public ResetPasswordResponse resetPassword(@Valid ResetPasswordRequest request) {
        log.info("Reset password request received for email: {}", request.getEmail());

        User userByEmail = userDataService.getUserByEmail(request.getEmail());
        log.debug("User retrieved for reset password: {}", userByEmail);

        userDataService.updatePasswordUser(userByEmail, request.getNewPassword());
        log.info("Password reset successfully for email: {}", request.getEmail());

        ResetPasswordResponse response = ResetPasswordResponse.builder()
                .email(userByEmail.getEmail())
                .message("Reset password successful")
                .build();
        log.info("Reset password response created for email: {}", userByEmail.getEmail());

        return response;
    }


}
