package ua.torque.nexus.feature.registration.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ua.torque.nexus.feature.notification.service.NotificationService;
import ua.torque.nexus.feature.registration.model.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void onRegistrationComplete(RegistrationCompleteEvent event) {
        User user = event.getUser();
        log.info("Handling RegistrationCompleteEvent for {}", user.getEmail());
        notificationService.sendConfirmationEmail(user);
    }
}
