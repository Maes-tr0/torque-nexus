package ua.torque.nexus.access.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionTest {

    @Test
    void noArgsConstructorShouldExistAndBePublic() throws Exception {
        Constructor<Permission> constructor = Permission.class.getDeclaredConstructor();
        assertThat(Modifier.isPublic(constructor.getModifiers())).isTrue();
        Permission permission = constructor.newInstance();
        assertThat(permission).isNotNull();
    }

    @Test
    void setIdShouldBePrivate() throws Exception {
        Method setter = Permission.class.getDeclaredMethod("setId", Long.class);
        assertThat(Modifier.isPrivate(setter.getModifiers())).isTrue();

        // verify reflection can still set it
        setter.setAccessible(true);
        Permission u = new Permission();
        setter.invoke(u, 123L);
        assertThat(u.getId()).isEqualTo(123L);
    }

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

        assertThat(p1)
                .hasSameHashCodeAs(p2)
                .isEqualTo(p2)
                .isNotEqualTo(p3)
                .isNotEqualTo(p4);
    }

    @Test
    void builderDoesNotSetId() {
        Permission permission = Permission.builder()
                .type(PermissionType.LOGIN)
                .description("Login permission")
                .build();
        assertThat(permission.getId()).isNull();
    }

    @Test
    void allPermissionTypesHaveNonNullNonEmptyDescription() {
        for (PermissionType type : PermissionType.values()) {
            assertThat(type.getDescription())
                    .as("Description for %s should not be blank", type)
                    .isNotNull()
                    .isNotEmpty();
        }
    }
}