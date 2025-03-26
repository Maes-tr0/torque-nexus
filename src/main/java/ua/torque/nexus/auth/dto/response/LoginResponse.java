package ua.torque.nexus.auth.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(String email, String token, String message) {
}
