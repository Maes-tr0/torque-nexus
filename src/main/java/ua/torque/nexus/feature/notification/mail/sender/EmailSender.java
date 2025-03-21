package ua.torque.nexus.feature.notification.mail.sender;

public interface EmailSender {
    void send(String to, String subject, String body);
}