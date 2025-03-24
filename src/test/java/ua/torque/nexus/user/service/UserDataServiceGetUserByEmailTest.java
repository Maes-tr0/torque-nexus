package ua.torque.nexus.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.torque.nexus.user.exception.UserNotFoundException;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDataService – getUserByEmail() testing")
class UserDataServiceGetUserByEmailTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDataService userDataService;

    private User testUser;

    @BeforeEach
    void beforeEach() {
        testUser = User.builder()
                .fullName("Test User")
                .email("test@example.com")
                .password("Password1234")
                .build();

        userRepository.save(testUser);
    }

    @Test
    void whenEmailExists_thenReturnUser() {
        when(userRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));

        User actual = userDataService.getUserByEmail(testUser.getEmail());

        assertSame(testUser, actual);
    }

    @Test
    void whenEmailNotFound_thenThrowUserNotFoundException() {
        String missingEmail = "missing@example.com";

        when(userRepository.findByEmail(missingEmail))
                .thenReturn(Optional.empty());

        var ex = assertThrows(
                UserNotFoundException.class,
                () -> userDataService.getUserByEmail(missingEmail)
        );

        assertTrue(ex.getMessage().contains(missingEmail));
    }
}
