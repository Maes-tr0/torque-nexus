package ua.torque.nexus.access.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ua.torque.nexus.access.model.Permission;
import ua.torque.nexus.access.model.PermissionType;
import ua.torque.nexus.access.model.Role;
import ua.torque.nexus.access.model.RoleType;
import ua.torque.nexus.access.repository.PermissionRepository;
import ua.torque.nexus.access.repository.RoleRepository;
import ua.torque.nexus.user.model.User;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccessControlServiceTest {

    private PermissionRepository permissionRepository;
    private RoleRepository roleRepository;

    private AccessControlService authService;

    @BeforeEach
    void setUp() {
        permissionRepository = org.mockito.Mockito.mock(PermissionRepository.class);
        roleRepository = org.mockito.Mockito.mock(RoleRepository.class);

        Set<Permission> defaultAdminPermissions = new HashSet<>();
        defaultAdminPermissions.add(Permission.builder()
                .type(PermissionType.REGISTER)
                .description("Registration permission")
                .build());
        defaultAdminPermissions.add(Permission.builder()
                .type(PermissionType.LOGIN)
                .description("Login permission")
                .build());

        Set<Permission> defaultUserPermissions = new HashSet<>();
        defaultUserPermissions.add(Permission.builder()
                .type(PermissionType.LOGIN)
                .description("Login permission")
                .build());

        authService = new AccessControlService(permissionRepository, roleRepository, defaultAdminPermissions, defaultUserPermissions);

        MockitoAnnotations.openMocks(this);
    }

    @Test
    void initializeRolesAndPermissions_whenRepositoriesEmpty_thenCreateEntities() {
        for (PermissionType permType : PermissionType.values()) {
            when(permissionRepository.existsByType(permType)).thenReturn(false);
            when(permissionRepository.save(any(Permission.class))).thenAnswer(invocation -> invocation.getArgument(0));
        }

        for (RoleType roleType : RoleType.values()) {
            when(roleRepository.findByType(roleType)).thenReturn(Optional.empty());
            when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));
        }

        authService.initializeRolesAndPermissions();

        for (PermissionType permType : PermissionType.values()) {
            verify(permissionRepository).existsByType(permType);
        }

        for (RoleType roleType : RoleType.values()) {
            verify(roleRepository).findByType(roleType);
        }
    }

    @Test
    void assignRoleToUser_whenRoleExists_thenAssignRole() {
        User user = new User();
        user.setEmail("john.doe@example.com");

        Role adminRole = Role.builder().type(RoleType.ADMIN).build();
        when(roleRepository.findByType(RoleType.ADMIN)).thenReturn(Optional.of(adminRole));

        authService.assignRoleToUser(user, RoleType.ADMIN);

        assertThat(user.getRole()).isEqualTo(adminRole);
        verify(roleRepository).findByType(RoleType.ADMIN);
    }

    @Test
    void assignRoleToUser_whenRoleNotFound_thenThrowException() {
        User user = new User();
        user.setEmail("jane.doe@example.com");

        when(roleRepository.findByType(RoleType.CUSTOMER)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                authService.assignRoleToUser(user, RoleType.CUSTOMER)
        );
        assertThat(exception.getMessage()).contains("Role not found");
    }
}
