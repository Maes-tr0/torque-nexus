package ua.torque.nexus.access.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    void noArgsConstructorCreatesRoleWithEmptyPermissions() {
        Role role = new Role();
        assertThat(role.getId()).isNull();
        assertThat(role.getPermissions()).isNotNull().isEmpty();
    }

    @Test
    void allArgsConstructorSetsFieldsCorrectly() {
        Set<Permission> perms = new HashSet<>();
        perms.add(Permission.builder().type(PermissionType.LOGIN).build());

        Role role = new Role(100L, RoleType.ADMIN, perms);

        assertThat(role.getId()).isEqualTo(100L);
        assertThat(role.getType()).isEqualTo(RoleType.ADMIN);
        assertThat(role.getPermissions()).hasSize(1).containsAll(perms);
    }

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
    void equalsAndHashCode_equalWhenSameIdAndType() {
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

        assertThat(role1).isEqualTo(role2).hasSameHashCodeAs(role2);
    }

    @Test
    void equalsAndHashCode_notEqualWhenDifferentId() {
        Set<Permission> permissions = new HashSet<>();
        Role role1 = Role.builder()
                .id(1L)
                .type(RoleType.ADMIN)
                .permissions(permissions)
                .build();
        Role role3 = Role.builder()
                .id(2L)
                .type(RoleType.ADMIN)
                .permissions(permissions)
                .build();

        assertThat(role1).isNotEqualTo(role3);
    }

    @Test
    void equalsAndHashCode_notEqualWhenDifferentType() {
        Role role1 = Role.builder()
                .id(1L)
                .type(RoleType.ADMIN)
                .permissions(new HashSet<>())
                .build();
        Role role4 = Role.builder()
                .id(1L)
                .type(RoleType.CUSTOMER)
                .permissions(new HashSet<>())
                .build();

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

    @Test
    void setIdShouldBePrivate() throws Exception {
        Method setter = Role.class.getDeclaredMethod("setId", Long.class);
        assertThat(Modifier.isPrivate(setter.getModifiers())).isTrue();

        // verify reflection can still set it
        setter.setAccessible(true);
        Role u = new Role();
        setter.invoke(u, 123L);
        assertThat(u.getId()).isEqualTo(123L);
    }

    @Test
    void allRoleTypesHaveNonNullNonEmptyDisplayName() {
        for (RoleType type : RoleType.values()) {
            assertThat(type.getDisplayName())
                    .as("DisplayName for %s should not be blank", type)
                    .isNotNull()
                    .isNotEmpty();
        }
    }
}
