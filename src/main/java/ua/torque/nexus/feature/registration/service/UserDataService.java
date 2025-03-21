package ua.torque.nexus.feature.registration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.torque.nexus.feature.registration.model.User;
import ua.torque.nexus.feature.registration.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDataService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void save(User user) {
        log.info("Attempting to register user with email={}", user.getEmail());

        if (userIsRegistered(user.getEmail())) {
            log.warn("Registration failed — user already registered: email={}", user.getEmail());
            throw new RuntimeException("User is already registered");
        }

        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        log.debug("Password encoded for email={}", user.getEmail());

        userRepository.saveAndFlush(user);
        log.info("User successfully saved: email={}", user.getEmail());
    }

    public boolean userIsRegistered(String email) {
        boolean exists = userRepository.findByEmail(email).isPresent();
        log.debug("User exists check for email={} => {}", email, exists);
        return exists;
    }
}
