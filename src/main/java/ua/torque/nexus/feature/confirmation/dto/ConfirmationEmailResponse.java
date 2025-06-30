package ua.torque.nexus.feature.confirmation.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ConfirmationEmailResponse(String email, LocalDateTime confirmedAt, String message) {
}