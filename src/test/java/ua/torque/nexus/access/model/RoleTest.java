package ua.torque.nexus.access.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    void builderCreatesRoleWithEmptyPermissions() {
        Role role = Role.builder()
                .type(RoleType.ADMIN)
                .permissions(new HashSet<>())
                .build();
        assertThat(role.getType()).isEqualTo(RoleType.ADMIN);
        assertThat(role.getPermissions()).isNotNull().isEmpty();
    }

    @Test
    void equalsAndHashCode_ignorePermissions() {
        Set<Permission> permissions = new HashSet<>();
        Role role1 = Role.builder()
                .id(1L)
                .type(RoleType.ADMIN)
                .permissions(permissions)
                .build();
        Role role2 = Role.builder()
                .id(1L)
                .type(RoleType.ADMIN)
                .permissions(new HashSet<>(permissions))
                .build();
        Role role3 = Role.builder()
                .id(2L)
                .type(RoleType.ADMIN)
                .permissions(new HashSet<>(permissions))
                .build();
        Role role4 = Role.builder()
                .id(1L)
                .type(RoleType.CUSTOMER)
                .permissions(new HashSet<>(permissions))
                .build();
        assertThat(role1).isEqualTo(role2);
        assertThat(role1.hashCode()).isEqualTo(role2.hashCode());
        assertThat(role1).isNotEqualTo(role3);
        assertThat(role1).isNotEqualTo(role4);
    }

    @Test
    void permissionsCanBeModified() {
        Role role = Role.builder()
                .id(1L)
                .type(RoleType.CUSTOMER)
                .permissions(new HashSet<>())
                .build();
        Permission perm1 = Permission.builder()
                .type(PermissionType.REGISTER)
                .description("Register permission")
                .build();
        Permission perm2 = Permission.builder()
                .type(PermissionType.LOGIN)
                .description("Login permission")
                .build();
        role.getPermissions().add(perm1);
        role.getPermissions().add(perm2);
        assertThat(role.getPermissions()).hasSize(2).containsExactlyInAnyOrder(perm1, perm2);
    }
}
