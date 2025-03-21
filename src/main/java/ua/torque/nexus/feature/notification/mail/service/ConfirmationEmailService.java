package ua.torque.nexus.feature.notification.mail.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.torque.nexus.feature.notification.mail.sender.EmailSender;
import ua.torque.nexus.feature.notification.mail.template.EmailTemplateProvider;
import ua.torque.nexus.feature.registration.model.User;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;
import ua.torque.nexus.feature.token.email.service.ConfirmationTokenDataService;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmationEmailService {
    private final ConfirmationTokenDataService tokenService;
    private final EmailTemplateProvider templateProvider;
    private final EmailSender emailSender;

    public void sendConfirmation(User user) {
        ConfirmationToken token = tokenService.getConfirmationToken(user);

        String body = templateProvider.build("confirmation", Map.of(
                "name", user.getFullName(),
                "link", buildLink(token.getToken())
        ));
        emailSender.send(user.getEmail(), "Підтвердіть email", body);
        log.info("Confirmation email sent to {}", user.getEmail());
    }

    private String buildLink(@NotBlank String token) {
        return "http://localhost:8080/api/v1/auth/confirm?token=%s".formatted(token);
    }
}
