package ua.torque.nexus.feature.token.email.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.torque.nexus.feature.token.email.dto.ConfirmationEmailRequest;
import ua.torque.nexus.feature.token.email.dto.ConfirmationEmailResponse;
import ua.torque.nexus.feature.token.email.mapper.ConfirmationEmailMapper;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;
import ua.torque.nexus.feature.token.email.service.ConfirmationTokenService;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ConfirmationEmailController {
    private final ConfirmationTokenService tokenService;
    private final ConfirmationEmailMapper confirmationEmailMapper;

    @GetMapping("/confirm")
    public ResponseEntity<ConfirmationEmailResponse> confirmEmail(
            @Valid ConfirmationEmailRequest request) {

        ConfirmationToken confirmedToken = tokenService.confirmToken(request.getToken());

        ConfirmationEmailResponse response = confirmationEmailMapper.tokenToResponse(confirmedToken);

        log.info("Email confirmed for token {}", confirmedToken.getToken());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(response);
    }
}
