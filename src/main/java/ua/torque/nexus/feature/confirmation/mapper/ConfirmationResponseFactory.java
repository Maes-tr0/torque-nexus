package ua.torque.nexus.feature.confirmation.mapper;

import ua.torque.nexus.feature.confirmation.dto.ConfirmationEmailResponse;

import java.time.LocalDateTime;


public final class ConfirmationResponseFactory {

    private ConfirmationResponseFactory() {
    }

    public static ConfirmationEmailResponse build(String email) {
        return ConfirmationEmailResponse.builder()
                .email(email)
                .message("Your email has been successfully confirmed.")
                .confirmedAt(LocalDateTime.now())
                .build();
    }
}