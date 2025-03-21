package ua.torque.nexus.feature.registration.model.dto;

import lombok.Builder;

@Builder
public record RegistrationResponse(String email, String message) {
}