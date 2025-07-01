package ua.torque.nexus.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.access.service.AccessControlService;
import ua.torque.nexus.common.exception.ExceptionType;
import ua.torque.nexus.security.service.JwtTokenService;
import ua.torque.nexus.common.exception.AuthorizationException;
import ua.torque.nexus.common.exception.InvalidInputException;
import ua.torque.nexus.common.exception.OperationFailedException;
import ua.torque.nexus.common.exception.DataConflictException;
import ua.torque.nexus.common.exception.DataNotFoundException;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.repository.UserRepository;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final AccessControlService accessControlService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;


    @Transactional
    public String createUser(User user) {
        log.debug("Attempting to create new user: {}", user.getEmail());

        validateUserDoesNotExist(user.getEmail());
        User preparedUser = prepareNewUser(user);

        User savedUser;
        try {
            savedUser = userRepository.save(preparedUser);
            log.info("User saved successfully. email={}", savedUser.getEmail());
        } catch (DataAccessException e) {
            log.error("Error saving user with email={}", user.getEmail(), e);
            throw new OperationFailedException(ExceptionType.USER_SAVE_FAILED, Map.of("email", user.getEmail()));
        }

        return jwtTokenService.generateConfirmationToken(savedUser);
    }

    @Transactional
    public User updatePasswordUser(String email, String newPassword) {
        log.debug("Initiating password update for user: {}", email);

        User user = getUserByEmail(email);

        validateUserIsConfirmed(user);
        validateNewPasswordIsDifferent(user, newPassword);

        try {
            user.setPassword(passwordEncoder.encode(newPassword));
            log.debug("Password encoded for user: {}", user.getEmail());

            User updatedUser = userRepository.save(user);
            log.info("Password updated successfully for user: {}", updatedUser.getEmail());
            return updatedUser;
        } catch (DataAccessException e) {
            log.error("Error updating password for user: {}", user.getEmail(), e);
            throw new OperationFailedException(ExceptionType.PASSWORD_UPDATE_FAILED, Map.of("email", user.getEmail()));
        }
    }

    @Transactional
    public void confirmAccount(String email) {
        User user = getUserByEmail(email);

        if (user.isEmailConfirmed()) {
            log.warn("Attempted to confirm an already confirmed account for email: {}", email);
            throw new DataConflictException(ExceptionType.EMAIL_ALREADY_CONFIRMED);
        }

        user.setEmailConfirmed(true);
        userRepository.save(user);

        log.info("Account for email {} has been confirmed.", user.getEmail());
    }


    public User getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("User not found by email: {}", email);
            return new DataNotFoundException(ExceptionType.USER_NOT_FOUND, Map.of("email", email));
        });
    }


    private void validateUserDoesNotExist(String email) {
        log.debug("Checking if user exists with email: {}", email);
        userRepository.findByEmail(email).ifPresent(user -> {
            log.warn("Attempt to register an existing email: {}", email);
            throw new DataConflictException(ExceptionType.USER_EMAIL_ALREADY_EXISTS);
        });
    }

    private User prepareNewUser(User user) {
        log.debug("Preparing new user {}: assigning role and encoding password.", user.getEmail());
        User userWithRole = accessControlService.assignDefaultRoleToUser(user);
        userWithRole.setPassword(passwordEncoder.encode(user.getPassword()));

        return userWithRole;
    }

    private void validateUserIsConfirmed(User user) {
        log.debug("Checking if user is confirmed: {}", user.getEmail());
        if (!user.isEmailConfirmed()) {
            log.warn("User is not confirmed: {}", user.getEmail());
            throw new AuthorizationException(ExceptionType.EMAIL_NOT_CONFIRMED);
        }
    }

    private void validateNewPasswordIsDifferent(User user, String newPassword) {
        log.debug("Checking if new password is same as old for user {}.", user.getEmail());
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            log.warn("New password is the same as the old one for user: {}", user.getEmail());
            throw new InvalidInputException(ExceptionType.SAME_PASSWORD);
        }
    }
}