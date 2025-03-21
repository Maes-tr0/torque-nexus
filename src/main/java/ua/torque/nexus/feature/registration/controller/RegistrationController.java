package ua.torque.nexus.feature.registration.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.torque.nexus.feature.token.email.model.dto.ConfirmationResponse;
import ua.torque.nexus.feature.registration.model.dto.RegistrationRequest;
import ua.torque.nexus.feature.registration.model.dto.RegistrationResponse;
import ua.torque.nexus.feature.registration.service.RegistrationService;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping("registration")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegistrationRequest request) {
        RegistrationResponse response = registrationService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("confirm")
    public ResponseEntity<ConfirmationResponse> confirmEmail(@RequestParam("token") String token) {
        try {
            //TODO: Fix interface not must "confirmEmail" but Basic realization must have this and integrate
            // correct implements
            ConfirmationResponse response = registrationService.confirmEmail(token);
            log.info("Email confirmed successfully for token: {}", token);

            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(response);
        } catch (Exception e){

        }
    }
}