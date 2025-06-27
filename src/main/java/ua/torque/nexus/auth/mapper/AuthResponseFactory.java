package ua.torque.nexus.auth.mapper;

import ua.torque.nexus.auth.dto.response.AuthResponse;
import ua.torque.nexus.auth.dto.response.ResetPasswordResponse;
import ua.torque.nexus.user.model.User;

public class AuthResponseFactory {

    private AuthResponseFactory() {}

    public static AuthResponse buildAuthResponse(User user, String jwt) {
        return AuthResponse.builder()
                .email(user.getEmail())
                .token(jwt)
                .message("Registration successful — please confirm your email")
                .build();
    }

    public static ResetPasswordResponse buildResetPasswordResponse(User user) {
        return ResetPasswordResponse.builder()
                .email(user.getEmail())
                .message("Password successfully changed")
                .build();
    }
}
