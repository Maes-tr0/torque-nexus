package ua.torque.nexus.feature.confirmation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.torque.nexus.feature.confirmation.dto.ConfirmationEmailResponse;
import ua.torque.nexus.feature.confirmation.service.EmailConfirmationService;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ConfirmationEmailController {

    private final EmailConfirmationService emailConfirmationService;


    @PostMapping("/confirm")
    public ResponseEntity<ConfirmationEmailResponse> confirmEmail(@RequestParam("token") String token) {
        log.info("POST /confirm request received.");

        ConfirmationEmailResponse response = emailConfirmationService.confirmEmail(token);

        log.info("<-Email-confirmation-> completed for email={}", response.email());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
