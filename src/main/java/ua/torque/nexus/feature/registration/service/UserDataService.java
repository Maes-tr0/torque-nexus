package ua.torque.nexus.feature.registration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.torque.nexus.feature.registration.model.User;
import ua.torque.nexus.feature.registration.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserDataService {
    private final UserRepository userRepository;


    public void save(User user) {
        if (userIsRegistered(user.getEmail())) {
            throw new RuntimeException("User is already registered");
        }

        userRepository.save(user);
    }

    public boolean userIsRegistered(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}