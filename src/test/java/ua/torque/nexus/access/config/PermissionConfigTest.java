package ua.torque.nexus.access.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ua.torque.nexus.access.model.Permission;
import ua.torque.nexus.access.model.PermissionType;
import ua.torque.nexus.access.repository.PermissionRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

class PermissionConfigTest {

    @Mock
    private PermissionRepository permissionRepository;

    private PermissionConfig permissionConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        permissionConfig = new PermissionConfig(permissionRepository);
    }

    @Test
    void defaultAdminPermissions_empty() {
        when(permissionRepository.findByTypeIn(anyList())).thenReturn(Collections.emptyList());
        Set<Permission> adminPermissions = permissionConfig.defaultAdminPermissions();
        assertThat(adminPermissions).isEmpty();
    }

    @Test
    void defaultAdminPermissions_nonEmpty() {
        Permission p1 = Permission.builder().type(PermissionType.LOGIN).description("desc1").build();
        Permission p2 = Permission.builder().type(PermissionType.LOGOUT).description("desc2").build();
        when(permissionRepository.findByTypeIn(anyList())).thenReturn(List.of(p1, p2));
        Set<Permission> adminPermissions = permissionConfig.defaultAdminPermissions();
        assertThat(adminPermissions).hasSize(2).containsExactlyInAnyOrder(p1, p2);
    }

    @Test
    void defaultUserPermissions_empty() {
        when(permissionRepository.findByTypeIn(anyList())).thenReturn(Collections.emptyList());
        Set<Permission> userPermissions = permissionConfig.defaultUserPermissions();
        assertThat(userPermissions).isEmpty();
    }

    @Test
    void defaultUserPermissions_nonEmpty() {
        Permission p1 = Permission.builder().type(PermissionType.REGISTER).description("desc1").build();
        Permission p2 = Permission.builder().type(PermissionType.CHANGE_PASSWORD).description("desc2").build();
        when(permissionRepository.findByTypeIn(anyList())).thenReturn(List.of(p1, p2));
        Set<Permission> userPermissions = permissionConfig.defaultUserPermissions();
        assertThat(userPermissions).hasSize(2).containsExactlyInAnyOrder(p1, p2);
    }
}
