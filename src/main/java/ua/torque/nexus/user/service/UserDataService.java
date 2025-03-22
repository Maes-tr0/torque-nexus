package ua.torque.nexus.user.service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.access.exception.PasswordUpdateException;
import ua.torque.nexus.access.exception.UserSaveException;
import ua.torque.nexus.access.model.RoleType;
import ua.torque.nexus.access.service.AccessControlService;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;
import ua.torque.nexus.feature.token.email.service.ConfirmationTokenService;
import ua.torque.nexus.user.exception.EmailNotConfirmedException;
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
    public ConfirmationToken saveNewUser(User user) {
        try {
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                throw new UserAlreadyRegisteredException("User already registered: " + user.getEmail());
            }

            accessControlService.assignRoleToUser(user, RoleType.CUSTOMER);

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            userRepository.save(user);
            log.info("User saved successfully: {}", user.getEmail());

            return confirmationTokenService.generateTokenForUser(user);
        } catch (Exception e) {
            log.error("Error saving user: {}", user.getEmail());
            throw new UserSaveException("Failed to save user");
        }
    }

    @Transactional
    public void updatePasswordUser(User user, String newPassword) {
        try {
            if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
                log.warn("User not found: {}", user.getEmail());
                throw new UserNotFoundException("User not found: " + user.getEmail());
            }

            if (!user.isEmailConfirmed()) {
                log.warn("User is not confirmed: {}", user.getEmail());
                throw new EmailNotConfirmedException("User is not confirmed: " + user.getEmail());
            }

            if (isSamePassword(user, newPassword)) {
                log.warn("Attempt to update password with the same value for user: {}", user.getEmail());
                throw new SamePasswordException("New password must be different from the old password");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            log.info("Password updated successfully for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error updating password for user: {}", user.getEmail());
            throw new PasswordUpdateException("Failed to update password");
        }
    }

    private boolean isSamePassword(User user, String newPassword) {
        log.info("Checking password match: raw='{}', storedHash='{}'", newPassword, user.getPassword());

        return passwordEncoder.matches(newPassword, user.getPassword());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
    }
}
