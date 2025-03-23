package ua.torque.nexus.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.torque.nexus.user.exception.UserNotFoundException;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDataServiceGetUserByEmailTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDataService userDataService;

    @Test
    void getUserByEmail_ShouldReturnUser_WhenUserExists() {
        String email = "found@example.com";
        User user = User.builder()
                .fullName("Found User")
                .email(email)
                .password("encodedPass")
                .phoneNumber("+1234567890")
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userDataService.getUserByEmail(email);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void getUserByEmail_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDataService.getUserByEmail(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found: " + email);
        verify(userRepository).findByEmail(email);
    }
}
