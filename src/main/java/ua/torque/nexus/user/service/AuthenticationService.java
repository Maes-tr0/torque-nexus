package ua.torque.nexus.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.common.exception.AuthenticationException;
import ua.torque.nexus.common.exception.AuthorizationException;
import ua.torque.nexus.common.exception.ExceptionType;
import ua.torque.nexus.common.exception.InvalidInputException;
import ua.torque.nexus.security.service.JwtTokenService;
import ua.torque.nexus.user.model.User;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;


    public String loginUser(String email, String password) {
        Authentication authentication;
        try {
            log.debug("Attempting to authenticate user: {}", email);
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (DisabledException e) {
            log.warn("Login attempt for disabled (unconfirmed) user '{}'", email);
            throw new AuthorizationException(ExceptionType.EMAIL_NOT_CONFIRMED);

        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user '{}' due to bad credentials.", email);
            throw new AuthenticationException(ExceptionType.INVALID_CREDENTIALS);

        } catch (org.springframework.security.core.AuthenticationException e) {
            log.error("An unexpected authentication error occurred for user '{}'", email, e);
            throw new AuthenticationException(ExceptionType.AUTHENTICATION_FAILED);
        }

        User user = (User) authentication.getPrincipal();


        log.info("User '{}' successfully authenticated.", email);
        return jwtTokenService.generateAuthorizationToken(user);
    }
}
