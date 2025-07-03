package ua.torque.nexus.auth.mapper;

import ua.torque.nexus.auth.dto.response.AuthResponse;
import ua.torque.nexus.auth.dto.response.ResetPasswordResponse;

public class AuthResponseFactory {

    private AuthResponseFactory() {
    }

    public static AuthResponse buildAuthResponse(String email, String jwt) {
        return AuthResponse.builder()
                .email(email)
                .token(jwt)
                .message("Registration successful — please confirm your email")
                .build();
    }

    public static ResetPasswordResponse buildResetPasswordResponse(String email) {
        return ResetPasswordResponse.builder()
                .email(email)
                .message("Password successfully changed")
                .build();
    }
}
