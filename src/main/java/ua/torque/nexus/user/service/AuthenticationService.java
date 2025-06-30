package ua.torque.nexus.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.common.exception.ExceptionType;
import ua.torque.nexus.security.service.JwtTokenService;
import ua.torque.nexus.common.exception.AuthenticationException;
import ua.torque.nexus.common.exception.AuthorizationException;
import ua.torque.nexus.user.model.User;

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
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user '{}' due to bad credentials.", email);
            throw new AuthenticationException(ExceptionType.INVALID_CREDENTIALS);
        }

        User user = (User) authentication.getPrincipal();

        if (!user.isEmailConfirmed()) {
            log.warn("Authorization successful, but user's email '{}' is not confirmed.", email);
            throw new AuthorizationException(ExceptionType.EMAIL_NOT_CONFIRMED);
        }

        log.info("User '{}' successfully authenticated.", email);
        return jwtTokenService.generateAuthorizationToken(user);
    }
}
