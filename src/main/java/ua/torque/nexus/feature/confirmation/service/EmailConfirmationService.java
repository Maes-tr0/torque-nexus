package ua.torque.nexus.feature.confirmation.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.feature.confirmation.dto.ConfirmationEmailResponse;
import ua.torque.nexus.feature.confirmation.mapper.ConfirmationResponseFactory;
import ua.torque.nexus.security.service.JwtTokenService;
import ua.torque.nexus.user.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailConfirmationService {

    private final JwtTokenService jwtTokenService;
    private final UserService userService;


    @Transactional
    public ConfirmationEmailResponse confirmEmail(String token) {
        log.info("Initiating email confirmation process.");

        Claims claims = jwtTokenService.validateConfirmationToken(token);
        String userEmail = claims.getSubject();

        log.debug("Token validated for user: {}", userEmail);

        userService.confirmAccount(userEmail);

        return ConfirmationResponseFactory.build(userEmail);
    }
}
