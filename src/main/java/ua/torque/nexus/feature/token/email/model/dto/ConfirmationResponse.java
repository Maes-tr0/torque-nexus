package ua.torque.nexus.feature.token.email.model.dto;

import java.time.LocalDateTime;

public record ConfirmationResponse(String token, LocalDateTime expires) {
}
