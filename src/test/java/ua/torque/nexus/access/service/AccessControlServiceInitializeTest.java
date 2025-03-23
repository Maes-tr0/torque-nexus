package ua.torque.nexus.access.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.torque.nexus.access.model.Permission;
import ua.torque.nexus.access.model.PermissionType;
import ua.torque.nexus.access.model.Role;
import ua.torque.nexus.access.model.RoleType;
import ua.torque.nexus.access.repository.PermissionRepository;
import ua.torque.nexus.access.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessControlServiceInitializeTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AccessControlService accessControlService;

    @Test
    void initializeRolesAndPermissions_ShouldCreateMissingPermissionsAndRoles() {
        for (PermissionType permType : PermissionType.values()) {
            when(permissionRepository.existsByName(permType.name())).thenReturn(false);
        }
        when(permissionRepository.save(any(Permission.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(roleRepository.findByName(RoleType.ADMIN.name())).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleType.CUSTOMER.name())).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Permission> allPermissions = List.of(
                new Permission(1L, "CHANGE_PASSWORD", "The user can change his password"),
                new Permission(2L, "DELETE_ACCOUNT", "The user can delete his account"),
                new Permission(3L, "DELETE_ACCOUNTS", "The user can delete accounts"),
                new Permission(4L, "UPDATE_PASSWORD", "The user can update his password")
        );
        when(permissionRepository.findAll()).thenReturn(allPermissions);

        List<Permission> customerPermissions = List.of(
                new Permission(1L, "CHANGE_PASSWORD", "The user can change his password"),
                new Permission(2L, "DELETE_ACCOUNT", "The user can delete his account"),
                new Permission(4L, "UPDATE_PASSWORD", "The user can update his password")
        );
        when(permissionRepository.customerPermission()).thenReturn(customerPermissions);

        accessControlService.initializeRolesAndPermissions();

        for (PermissionType permType : PermissionType.values()) {
            verify(permissionRepository).existsByName(permType.name());
            verify(permissionRepository).save(argThat(permission ->
                    permission.getName().equals(permType.name())
                            && permission.getDescription().equals(permType.getDescription())
            ));
        }
        verify(roleRepository).findByName(RoleType.ADMIN.name());
        verify(roleRepository).findByName(RoleType.CUSTOMER.name());
        verify(roleRepository, atLeast(2)).save(any(Role.class));
    }
}
