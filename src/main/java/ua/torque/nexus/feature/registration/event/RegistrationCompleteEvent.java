package ua.torque.nexus.feature.registration.event;

import ua.torque.nexus.feature.registration.model.User;

public class RegistrationCompleteEvent {
    private final User user;

    public RegistrationCompleteEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
