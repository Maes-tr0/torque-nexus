package ua.torque.nexus.access.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionTest {

    @Test
    void builderShouldCreatePermissionWithGivenValues() {
        Permission permission = Permission.builder()
                .type(PermissionType.LOGIN)
                .description("Login permission")
                .build();
        assertThat(permission.getType()).isEqualTo(PermissionType.LOGIN);
        assertThat(permission.getDescription()).isEqualTo("Login permission");
        assertThat(permission.getId()).isNull();
    }

    @Test
    void equalsAndHashCodeShouldWorkAsExpected() {
        Permission p1 = Permission.builder()
                .id(1L)
                .type(PermissionType.LOGIN)
                .description("Login permission")
                .build();
        Permission p2 = Permission.builder()
                .id(1L)
                .type(PermissionType.LOGIN)
                .description("Login permission")
                .build();
        Permission p3 = Permission.builder()
                .id(2L)
                .type(PermissionType.LOGIN)
                .description("Login permission")
                .build();
        Permission p4 = Permission.builder()
                .id(1L)
                .type(PermissionType.LOGOUT)
                .description("Logout permission")
                .build();
        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
        assertThat(p1).isNotEqualTo(p3);
        assertThat(p1).isNotEqualTo(p4);
    }

    @Test
    void builderDoesNotSetId() {
        Permission permission = Permission.builder()
                .type(PermissionType.LOGIN)
                .description("Login permission")
                .build();
        assertThat(permission.getId()).isNull();
    }
}
