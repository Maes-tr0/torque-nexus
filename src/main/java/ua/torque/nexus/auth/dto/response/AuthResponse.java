package ua.torque.nexus.auth.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse(String email, String token, String message) {
}
