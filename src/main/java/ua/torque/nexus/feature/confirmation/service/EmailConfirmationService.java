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
        log.info("event=email_confirmation_flow_started");

        Claims claims = jwtTokenService.validateConfirmationToken(token);
        String userEmail = claims.getSubject();
        log.debug("event=token_validated email={}", userEmail);

        userService.confirmAccount(userEmail);

        log.info("event=email_confirmation_flow_finished status=success email={}", userEmail);
        return ConfirmationResponseFactory.build(userEmail);
    }
}