package ua.torque.nexus.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import ua.torque.nexus.access.model.Permission;
import ua.torque.nexus.access.model.PermissionType;
import ua.torque.nexus.access.model.Role;
import ua.torque.nexus.access.model.RoleType;
import ua.torque.nexus.user.exception.UserNotFoundException;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDataService – loadUserByUsername() testing")
class UserDataServiceLoadUserByUsernameTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDataService userDataService;

    @Test
    void whenUserExists_thenReturnUserDetails() {
        User user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .build();
        Role role = Role.builder().type(RoleType.CUSTOMER).build();
        Permission perm = Permission.builder().type(PermissionType.LOGIN).build();
        role.setPermissions(Set.of(perm));
        user.setRole(role);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetails details = userDataService.loadUserByUsername("test@example.com");

        assertEquals("test@example.com", details.getUsername());
        assertEquals("encodedPassword", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER")));
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(PermissionType.LOGIN.name())));
    }

    @Test
    void whenUserNotFound_thenThrowUserNotFoundException() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userDataService.loadUserByUsername("missing@example.com")
        );
    }

}
