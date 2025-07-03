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
import ua.torque.nexus.security.service.JwtTokenService;
import ua.torque.nexus.user.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;


    public String loginUser(String email, String password) {
        log.info("event=authentication_started email={}", email);
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (DisabledException e) {
            log.warn("event=authentication_failed status=failure reason=\"User account is disabled (email not confirmed)\" email={}", email);
            throw new AuthorizationException(ExceptionType.EMAIL_NOT_CONFIRMED);

        } catch (BadCredentialsException e) {
            log.warn("event=authentication_failed status=failure reason=\"Invalid credentials\" email={}", email);
            throw new AuthenticationException(ExceptionType.INVALID_CREDENTIALS);

        } catch (org.springframework.security.core.AuthenticationException e) {
            log.error("event=authentication_failed status=failure reason=\"Unexpected authentication error\" email={}", email, e);
            throw new AuthenticationException(ExceptionType.AUTHENTICATION_FAILED);
        }

        User user = (User) authentication.getPrincipal();

        log.info("event=authentication_finished status=success email={}", email);
        return jwtTokenService.generateAuthorizationToken(user);
    }
}