package ua.torque.nexus.feature.registration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.feature.registration.model.User;
import ua.torque.nexus.feature.registration.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDataService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void save(User user) {
        log.info("Attempting to register user with email={}", user.getEmail());

        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        log.debug("Password encoded for email={}", user.getEmail());

        try {
            userRepository.saveAndFlush(user);
            log.info("User successfully saved: email={}", user.getEmail());
        } catch (DataIntegrityViolationException ex) {
            log.error("Registration failed — user already registered: email={}", user.getEmail(), ex);
            throw new RuntimeException("User is already registered", ex);
        }
    }

    public boolean userIsRegistered(String email) {
        boolean exists = userRepository.findByEmail(email).isPresent();
        log.debug("User exists check for email={} => {}", email, exists);
        return exists;
    }
}
