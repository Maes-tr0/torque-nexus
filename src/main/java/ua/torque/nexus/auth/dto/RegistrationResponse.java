package ua.torque.nexus.auth.dto;

import lombok.Builder;

@Builder
public record RegistrationResponse(String email, String token, String message) {
}
