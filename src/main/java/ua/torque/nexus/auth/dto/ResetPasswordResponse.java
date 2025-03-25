package ua.torque.nexus.auth.dto;

import lombok.Builder;

@Builder
public record ResetPasswordResponse(String email, String message) {
}
