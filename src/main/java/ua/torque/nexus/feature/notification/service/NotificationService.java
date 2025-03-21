package ua.torque.nexus.feature.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.torque.nexus.feature.notification.mail.service.ConfirmationEmailService;
import ua.torque.nexus.feature.registration.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final ConfirmationEmailService confirmationEmailService;

    public void sendConfirmationEmail(User user) {
        confirmationEmailService.sendConfirmation(user);
    }
}
