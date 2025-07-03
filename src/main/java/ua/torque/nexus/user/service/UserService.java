package ua.torque.nexus.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.access.service.AccessControlService;
import ua.torque.nexus.common.exception.AuthenticationException;
import ua.torque.nexus.common.exception.DataConflictException;
import ua.torque.nexus.common.exception.DataNotFoundException;
import ua.torque.nexus.common.exception.ExceptionType;
import ua.torque.nexus.common.exception.InvalidInputException;
import ua.torque.nexus.common.exception.OperationFailedException;
import ua.torque.nexus.security.service.JwtTokenService;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.repository.UserRepository;

import java.util.Map;
import java.util.Optional;

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
        log.info("event=user_creation_started email={}", user.getEmail());
        validateUserDoesNotExist(user.getEmail());
        User preparedUser = prepareNewUser(user);

        User savedUser;
        try {
            savedUser = userRepository.save(preparedUser);
            log.info("event=user_creation_finished status=success userId={} email={}", savedUser.getId(), savedUser.getEmail());
        } catch (DataAccessException e) {
            log.error("event=user_creation_failed status=failure reason=\"Database access error\" email={}", user.getEmail(), e);
            throw new OperationFailedException(ExceptionType.USER_SAVE_FAILED, Map.of("email", user.getEmail()));
        }

        return jwtTokenService.generateConfirmationToken(savedUser);
    }

    @Transactional
    public void handlePasswordResetRequest(String email, String newPassword) {
        log.info("event=password_reset_request_started email={}", email);

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty() || !userOptional.get().isEmailConfirmed()) {
            log.warn("event=password_reset_request_aborted reason=\"User not found or not confirmed, not revealing error to client\" email={}", email);
            return;
        }

        User user = userOptional.get();
        validateNewPasswordIsDifferent(user, newPassword);

        try {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            log.info("event=password_reset_request_finished status=success email={}", user.getEmail());
        } catch (DataAccessException e) {
            log.error("event=password_reset_request_failed status=failure reason=\"Database access error\" email={}", email, e);
            throw new OperationFailedException(ExceptionType.PASSWORD_UPDATE_FAILED, Map.of("email", user.getEmail()));
        }
    }

    @Transactional
    public void confirmAccount(String email) {
        log.info("event=account_confirmation_started email={}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("event=account_confirmation_failed reason=\"User from valid token not found in DB\" email={}", email);
                    return new AuthenticationException(ExceptionType.TOKEN_INVALID, "User from token not found");
                });

        if (user.isEmailConfirmed()) {
            log.warn("event=account_confirmation_failed status=failure reason=\"Account already confirmed\" email={}", email);
            throw new DataConflictException(ExceptionType.EMAIL_ALREADY_CONFIRMED);
        }

        user.setEmailConfirmed(true);
        userRepository.save(user);
        log.info("event=account_confirmation_finished status=success email={}", user.getEmail());
    }

    public User getUserByEmail(String email) {
        log.debug("event=user_fetch_by_email_started email={}", email);
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("event=user_fetch_by_email_failed status=failure reason=\"User not found\" email={}", email);
            return new DataNotFoundException(ExceptionType.USER_NOT_FOUND, Map.of("email", email));
        });
    }


    private void validateUserDoesNotExist(String email) {
        log.debug("event=user_existence_check_started email={}", email);
        userRepository.findByEmail(email).ifPresent(user -> {
            log.warn("event=user_existence_check_failed status=failure reason=\"Email already exists\" email={}", email);
            throw new DataConflictException(ExceptionType.USER_EMAIL_ALREADY_EXISTS);
        });
    }

    private User prepareNewUser(User user) {
        log.debug("event=user_preparation_started email={}", user.getEmail());
        User userWithRole = accessControlService.assignDefaultRoleToUser(user);
        userWithRole.setPassword(passwordEncoder.encode(user.getPassword()));
        return userWithRole;
    }

    private void validateNewPasswordIsDifferent(User user, String newPassword) {
        log.debug("event=password_difference_check_started email={}", user.getEmail());
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            log.warn("event=password_difference_check_failed status=failure reason=\"New password is the same as the old one\" email={}", user.getEmail());
            throw new InvalidInputException(ExceptionType.SAME_PASSWORD);
        }
    }
}