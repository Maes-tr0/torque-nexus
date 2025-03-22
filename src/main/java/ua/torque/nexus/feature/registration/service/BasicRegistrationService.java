package ua.torque.nexus.feature.registration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ua.torque.nexus.feature.token.email.model.dto.ConfirmationResponse;
import ua.torque.nexus.feature.registration.event.RegistrationCompleteEvent;
import ua.torque.nexus.feature.registration.model.User;
import ua.torque.nexus.feature.registration.model.dto.RegistrationRequest;
import ua.torque.nexus.feature.registration.model.dto.RegistrationResponse;
import ua.torque.nexus.feature.registration.model.mapper.RegistrationMapper;
import ua.torque.nexus.feature.token.email.service.ConfirmationTokenDataService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicRegistrationService implements RegistrationService, EmailConfirmationService {
    private final UserDataService userDataService;
    private final RegistrationMapper registrationMapper;
    private final ConfirmationTokenDataService confirmationTokenDataService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public RegistrationResponse registerUser(RegistrationRequest request) {
        log.info("Received registration request for email={}", request.getEmail());

        User user = requestToUser(request);
        userDataService.save(user);

        confirmationTokenDataService.saveConfirmationToken(user);

        eventPublisher.publishEvent(new RegistrationCompleteEvent(user));

        RegistrationResponse response = userToRegistrationResponse(user);
        log.info("Registration completed for email={}", response.email());
        return response;
    }

    @Override
    public User requestToUser(RegistrationRequest request) {
        log.debug("Mapping RegistrationRequest to User for email={}", request.getEmail());
        return Optional.ofNullable(registrationMapper.toUser(request))
                .orElseThrow(() -> {
                    log.error("Mapping failed: RegistrationRequest → User for email={}", request.getEmail());
                    return new RuntimeException("Failed to convert RegistrationRequest to User.");
                });
    }

    @Override
    public RegistrationResponse userToRegistrationResponse(User user) {
        log.debug("Mapping User → RegistrationResponse for userId={}", user.getId());
        return Optional.ofNullable(registrationMapper.toUserRegistrationResponse(user))
                .orElseThrow(() -> {
                    log.error("Mapping failed: User → RegistrationResponse for userId={}", user.getId());
                    return new RuntimeException("Failed to convert User to RegistrationResponse.");
                });
    }

    public ConfirmationResponse confirmEmail(String token){
        return confirmationTokenDataService.confirmToken(token);
    }
}