package ua.torque.nexus.feature.token.email.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ConfirmationEmailResponse(String token, LocalDateTime confirmedAt, String message) {
}
