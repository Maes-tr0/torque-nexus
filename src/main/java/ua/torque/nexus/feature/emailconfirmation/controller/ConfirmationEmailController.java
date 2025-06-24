package ua.torque.nexus.feature.emailconfirmation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.torque.nexus.feature.emailconfirmation.dto.ConfirmationEmailResponse;
import ua.torque.nexus.feature.emailconfirmation.service.EmailConfirmationService;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ConfirmationEmailController {
    private final EmailConfirmationService emailConfirmationService;

    @PostMapping("/confirm")
    public ResponseEntity<ConfirmationEmailResponse> confirmEmail(
            @RequestParam("token") String token) {

        emailConfirmationService.confirmEmail(token);

        log.info("Email confirmed for token {}", token);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }
}
