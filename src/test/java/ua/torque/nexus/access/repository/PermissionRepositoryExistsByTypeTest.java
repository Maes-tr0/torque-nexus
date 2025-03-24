package ua.torque.nexus.access.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ua.torque.nexus.access.model.Permission;
import ua.torque.nexus.access.model.PermissionType;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(
        properties = {
                "spring.flyway.enabled=false",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
@DisplayName("PermissionRepository — testExistsByType() testing")
class PermissionRepositoryExistsByTypeTest {

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    void testExistsByType() {
        Permission permission = Permission.builder()
                .type(PermissionType.LOGIN)
                .description("Login permission")
                .build();
        permissionRepository.save(permission);
        boolean exists = permissionRepository.existsByType(PermissionType.LOGIN);
        assertThat(exists).isTrue();
        boolean notExists = permissionRepository.existsByType(PermissionType.LOGOUT);
        assertThat(notExists).isFalse();
    }
}
