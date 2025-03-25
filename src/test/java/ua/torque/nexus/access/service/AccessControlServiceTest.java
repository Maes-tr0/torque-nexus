package ua.torque.nexus.access.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.torque.nexus.access.model.Permission;
import ua.torque.nexus.access.model.PermissionType;
import ua.torque.nexus.access.model.Role;
import ua.torque.nexus.access.model.RoleType;
import ua.torque.nexus.access.repository.PermissionRepository;
import ua.torque.nexus.access.repository.RoleRepository;
import ua.torque.nexus.user.model.User;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessControlServiceTest {

    private PermissionRepository permissionRepository;
    private RoleRepository roleRepository;
    private AccessControlService service;

    @BeforeEach
    void setUp() {
        permissionRepository = mock(PermissionRepository.class);
        roleRepository = mock(RoleRepository.class);

        var adminPerms = Set.of(
                Permission.builder().type(PermissionType.REGISTER).description("desc").build(),
                Permission.builder().type(PermissionType.LOGIN).description("desc").build()
        );
        var userPerms = Set.of(
                Permission.builder().type(PermissionType.LOGIN).description("desc").build()
        );

        service = new AccessControlService(permissionRepository, roleRepository, adminPerms, userPerms);
    }

    @Test
    void initializeRoles_createsMissingPermissionsAndRoles() {
        when(permissionRepository.existsByType(any())).thenReturn(false);
        when(permissionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(roleRepository.findByType(any())).thenReturn(Optional.empty());
        when(roleRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.initializeRolesAndPermissions();

        verify(permissionRepository, times(PermissionType.values().length)).save(any());

        for (RoleType type : RoleType.values()) {
            verify(roleRepository).findByType(type);
            verify(roleRepository, times(2))
                    .save(argThat(role -> role.getType() == type));
        }
    }


    @Test
    void initializeRoles_skipsExistingPermissionAndUpdatesRolePermissions() {
        when(permissionRepository.existsByType(any())).thenReturn(true);
        Role existing = Role.builder().type(RoleType.CUSTOMER).build();
        when(roleRepository.findByType(RoleType.CUSTOMER)).thenReturn(Optional.of(existing));
        when(roleRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.initializeRolesAndPermissions();

        verify(permissionRepository, never()).save(any());
        assertThat(existing.getPermissions()).isNotEmpty();
        verify(roleRepository).save(existing);
    }

    @Test
    void assignRoleToUser_assignsWhenFound() {
        User user = new User();
        Role r = Role.builder().type(RoleType.ADMIN).build();
        when(roleRepository.findByType(RoleType.ADMIN)).thenReturn(Optional.of(r));

        service.assignRoleToUser(user, RoleType.ADMIN);

        assertThat(user.getRole()).isEqualTo(r);
    }

    @Test
    void assignRoleToUser_throwsWhenMissing() {
        User user = new User();
        when(roleRepository.findByType(RoleType.CUSTOMER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignRoleToUser(user, RoleType.CUSTOMER))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Role not found");
    }
}
