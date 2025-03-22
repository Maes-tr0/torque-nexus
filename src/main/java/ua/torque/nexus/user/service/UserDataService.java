package ua.torque.nexus.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.access.model.RoleType;
import ua.torque.nexus.access.service.AccessControlService;
import ua.torque.nexus.feature.token.email.service.ConfirmationTokenService;
import ua.torque.nexus.user.exception.SamePasswordException;
import ua.torque.nexus.user.exception.UserAlreadyRegisteredException;
import ua.torque.nexus.user.exception.UserNotFoundException;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDataService {

    private final UserRepository userRepository;
    private final AccessControlService accessControlService;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Transactional
    public void saveNewUser(User user) {
        try {
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                throw new UserAlreadyRegisteredException("User already registered: " + user.getEmail());
            }

            accessControlService.assignRoleToUser(user, RoleType.CUSTOMER);

            userRepository.save(user);
            log.info("User saved successfully: {}", user.getEmail());

            confirmationTokenService.generateTokenForUser(user);
        } catch (Exception e) {
            log.error("Error saving user: {}", user.getEmail());
            throw new RuntimeException("Failed to save user");
        }
    }

    @Transactional
    public void updatePasswordUser(User user, String newPassword) {
        try {
            if (isSamePassword(user, newPassword)) {
                log.warn("Attempt to update password with the same value for user: {}", user.getEmail());
                throw new SamePasswordException("New password must be different from the old password");
            }

            if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
                log.warn("User not found: {}", user.getEmail());
                throw new UserNotFoundException("User not found: " + user.getEmail());
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            log.info("Password updated successfully for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error updating password for user: {}", user.getEmail());
            throw new RuntimeException("Failed to update password");
        }
    }

    private boolean isSamePassword(User user, String newPassword) {
        return passwordEncoder.matches(newPassword, user.getPassword());
    }
}
