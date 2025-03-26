package ua.torque.nexus.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.access.exception.PasswordUpdateException;
import ua.torque.nexus.access.exception.UserSaveException;
import ua.torque.nexus.access.model.Role;
import ua.torque.nexus.access.model.RoleType;
import ua.torque.nexus.access.service.AccessControlService;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;
import ua.torque.nexus.feature.token.email.service.ConfirmationTokenService;
import ua.torque.nexus.security.JwtTokenService;
import ua.torque.nexus.user.exception.InvalidCredentialsException;
import ua.torque.nexus.user.exception.SamePasswordException;
import ua.torque.nexus.user.exception.UserAlreadyExistsAndConfirmedException;
import ua.torque.nexus.user.exception.UserAlreadyExistsButUnconfirmedException;
import ua.torque.nexus.user.exception.UserNotFoundException;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.repository.UserRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDataService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AccessControlService accessControlService;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final JwtTokenService jwtTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);

        Set<GrantedAuthority> authorities = new HashSet<>();

        Role role = user.getRole();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getType()));

        role.getPermissions().forEach(permission ->
                authorities.add(new SimpleGrantedAuthority(permission.getType().name())));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    @Transactional
    public ConfirmationToken saveNewUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            if (!existingUser.get().isEmailConfirmed()) {
                throw new UserAlreadyExistsButUnconfirmedException(user.getEmail());
            }
            throw new UserAlreadyExistsAndConfirmedException(user.getEmail());
        }

        try {
            accessControlService.assignRoleToUser(user, RoleType.CUSTOMER);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            log.info("User saved successfully: {}", user.getEmail());
            return confirmationTokenService.generateTokenForUser(user);
        } catch (Exception e) {
            log.error("Error saving user: {}", user.getEmail(), e);
            throw new UserSaveException(
                    "Failed to save user: " + user.getEmail(),
                    Map.of("cause", e.getClass().getSimpleName(), "message", e.getMessage())
            );
        }
    }

    @Transactional
    public void updatePasswordUser(User user, String newPassword) {
        if (!user.isEmailConfirmed()) {
            log.warn("User is not confirmed: {}", user.getEmail());
            throw new UserNotFoundException(user.getEmail());
        }

        if (isSamePassword(user.getPassword(), newPassword)) {
            log.warn("Attempt to update password with the same value for user: {}", user.getEmail());
            throw new SamePasswordException();
        }

        try {
            user.setPassword(passwordEncoder.encode(newPassword));

            userRepository.save(user);
            log.info("Password updated successfully for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error updating password for user: {}", user.getEmail(), e);
            throw new PasswordUpdateException(
                    "Failed to update password for user: " + user.getEmail(),
                    Map.of(
                            "cause", e.getClass().getSimpleName(),
                            "message", e.getMessage()
                    )
            );
        }
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    protected boolean isSamePassword(String existPassword, String newPassword) {
        log.info("Checking password match: raw='{}', storedHash='{}'", newPassword, existPassword);

        return passwordEncoder.matches(newPassword, existPassword);
    }

    public String loginUser(String email, String password) {
        log.info("Attempting to log in user with email: {}", email);

        User userByEmail = getUserByEmail(email);
        log.debug("User with email '{}' found in DB, checking confirmation status...", email);

        if (!userByEmail.isEmailConfirmed()) {
            log.warn("User with email '{}' is not confirmed. Throwing exception.", email);
            throw new UserNotFoundException(email);
        }

        log.debug("Checking password for user '{}'", email);
        if (!isSamePassword(userByEmail.getPassword(), password)) {
            log.warn("Invalid credentials provided for user '{}'. Throwing exception.", email);
            throw new InvalidCredentialsException();
        }

        log.info("User with email '{}' successfully logged in. Generating token...", email);
        return jwtTokenService.generateTokenForUser(userByEmail);
    }

}
