package ua.torque.nexus.auth.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.torque.nexus.auth.dto.request.ResetPasswordRequest;
import ua.torque.nexus.auth.dto.response.ResetPasswordResponse;
import ua.torque.nexus.auth.mapper.AuthResponseFactory;
import ua.torque.nexus.user.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserService userService;


    public ResetPasswordResponse processPasswordReset(@Valid ResetPasswordRequest request) {
        log.info("event=password_reset_flow_started email={}", request.getEmail());

        userService.handlePasswordResetRequest(request.getEmail(), request.getNewPassword());

        log.info("event=password_reset_flow_finished status=success email={}", request.getEmail());
        return AuthResponseFactory.buildResetPasswordResponse(request.getEmail());
    }
}