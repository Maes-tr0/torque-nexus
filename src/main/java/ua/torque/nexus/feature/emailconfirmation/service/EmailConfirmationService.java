package ua.torque.nexus.feature.emailconfirmation.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.feature.emailconfirmation.exception.EmailAlreadyConfirmedException;
import ua.torque.nexus.security.JwtTokenService;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.service.UserDataService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailConfirmationService {

    private final JwtTokenService jwtTokenService;
    private final UserDataService userDataService;

    @Transactional
    public void confirmEmail(String token) {
        Claims claims = jwtTokenService.validateToken(token);
        String userEmail = claims.getSubject();
        log.info("Token validated for user: {}", userEmail);

        User user = userDataService.getUserByEmail(userEmail);
        if (user.isEmailConfirmed()) {
            throw new EmailAlreadyConfirmedException("User email is already confirmed");
        }

        userDataService.markAccountAsConfirmed(user);
        log.info("User {} email confirmed", userEmail);
    }
}
