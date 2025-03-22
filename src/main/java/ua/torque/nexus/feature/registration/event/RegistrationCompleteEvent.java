package ua.torque.nexus.feature.registration.event;

import ua.torque.nexus.feature.registration.model.User;

public record RegistrationCompleteEvent(User user) {
}
