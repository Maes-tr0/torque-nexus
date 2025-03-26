package ua.torque.nexus.auth.dto.response;

import lombok.Builder;

@Builder
public record RegistrationResponse(String email, String token, String message) {
}
