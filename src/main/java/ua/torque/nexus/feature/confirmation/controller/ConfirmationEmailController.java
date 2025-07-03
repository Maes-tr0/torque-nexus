package ua.torque.nexus.feature.confirmation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.info("event=request_received httpMethod=POST path=/api/v1/auth/confirm");

        ConfirmationEmailResponse response = emailConfirmationService.confirmEmail(token);

        log.info("event=request_completed httpMethod=POST path=/api/v1/auth/confirm status=success httpStatus=200 email={}",
                response.email());
        return ResponseEntity.ok(response);
    }
}