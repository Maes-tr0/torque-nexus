package ua.torque.nexus.access.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ua.torque.nexus.access.model.Permission;
import ua.torque.nexus.access.model.PermissionType;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(
        properties = {
                "spring.flyway.enabled=false",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
@DisplayName("PermissionRepository — testFindByTypeIn() testing")
class PermissionRepositoryFindByTypeInTest {

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    void testFindByTypeIn() {
        Permission p1 = Permission.builder()
                .type(PermissionType.LOGIN)
                .description("Login permission")
                .build();
        Permission p2 = Permission.builder()
                .type(PermissionType.LOGOUT)
                .description("Logout permission")
                .build();
        Permission p3 = Permission.builder()
                .type(PermissionType.REGISTER)
                .description("Register permission")
                .build();
        permissionRepository.save(p1);
        permissionRepository.save(p2);
        permissionRepository.save(p3);
        Set<PermissionType> types = EnumSet.of(PermissionType.LOGIN, PermissionType.REGISTER);
        List<Permission> result = permissionRepository.findByTypeIn(types);
        assertThat(result).hasSize(2)
                .extracting(Permission::getType)
                .containsExactlyInAnyOrder(PermissionType.LOGIN, PermissionType.REGISTER);
    }
}
