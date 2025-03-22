package ua.torque.nexus.feature.token.email.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class ConfirmationEmailRequest{
        @NotBlank(message = "Token must not be blank")
        String token;
}