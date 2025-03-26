package ua.torque.nexus.auth.dto.response;

import lombok.Builder;

@Builder
public record ResetPasswordResponse(String email, String message) {
}
