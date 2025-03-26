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
import ua.torque.nexus.user.exception.EmailNotConfirmedException;
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

    private boolean isSamePassword(String existPassword, String newPassword) {
        log.info("Checking password match: raw='{}', storedHash='{}'", newPassword, existPassword);

        return passwordEncoder.matches(newPassword, existPassword);
    }
}
