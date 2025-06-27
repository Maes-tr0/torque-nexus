package ua.torque.nexus.auth.service;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.torque.nexus.auth.dto.request.ResetPasswordRequest;
import ua.torque.nexus.auth.dto.response.ResetPasswordResponse;
import ua.torque.nexus.auth.mapper.AuthResponseFactory;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserService userService;

    public ResetPasswordResponse resetUserPassword(@Valid ResetPasswordRequest request) {
        log.info("Reset-password start for email={}", request.getEmail());

        User user = userService.getUserByEmail(request.getEmail());
        log.debug("User fetched for reset: email={}, confirmed={}, role={}",
                user.getEmail(), user.isEmailConfirmed(), user.getRole().getType());

        User updatedPasswordUser = userService.updatePasswordUser(user, request.getNewPassword());
        log.info("Password updated for email={}", request.getEmail());

        return AuthResponseFactory.buildResetPasswordResponse(updatedPasswordUser);
    }
}
