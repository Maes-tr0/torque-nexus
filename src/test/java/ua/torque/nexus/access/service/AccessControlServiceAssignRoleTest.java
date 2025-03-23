package ua.torque.nexus.access.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.torque.nexus.access.model.Role;
import ua.torque.nexus.access.model.RoleType;
import ua.torque.nexus.access.repository.RoleRepository;
import ua.torque.nexus.user.model.User;

@ExtendWith(MockitoExtension.class)
class AccessControlServiceAssignRoleTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AccessControlService accessControlService;

    @Test
    void assignRoleToUser_ShouldAssignRole_WhenRoleExists() {
        User user = User.builder()
                .fullName("Test User")
                .email("test@example.com")
                .build();
        Role role = Role.builder()
                .name("CUSTOMER")
                .build();
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.of(role));

        accessControlService.assignRoleToUser(user, RoleType.CUSTOMER);

        assertThat(user.getRole()).isEqualTo(role);
        verify(roleRepository).findByName("CUSTOMER");
    }

    @Test
    void assignRoleToUser_ShouldThrowRuntimeException_WhenRoleNotFound() {
        User user = User.builder()
                .fullName("Test User")
                .email("test@example.com")
                .build();
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accessControlService.assignRoleToUser(user, RoleType.CUSTOMER))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Role not found: CUSTOMER");
        verify(roleRepository).findByName("CUSTOMER");
    }
}
