package ua.torque.nexus.feature.emailconfirmation.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ConfirmationEmailResponse(String token, LocalDateTime confirmedAt, String message) {
}
