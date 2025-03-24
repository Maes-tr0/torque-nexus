package ua.torque.nexus.user.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ua.torque.nexus.access.model.Role;
import ua.torque.nexus.access.model.RoleType;
import ua.torque.nexus.access.repository.RoleRepository;
import ua.torque.nexus.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(
        properties = {
                "spring.flyway.enabled=false",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
@DisplayName("UserRepository — findByEmail() testing")
class UserRepositoryFindByEmailTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void whenEmailExists_thenReturnUser() {
        Role savedRole = roleRepository.save(Role.builder()
                .type(RoleType.CUSTOMER)
                .build());

        User user = User.builder()
                .fullName("Test User")
                .email("exists@example.com")
                .password("secret")
                .build();
        user.setRole(savedRole);
        user.setEmailConfirmed(true);

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail(user.getEmail());

        assertThat(found).isPresent()
                .get().isEqualTo(user);
    }

    @Test
    void whenEmailNotExists_thenReturnEmpty() {
        Optional<User> found = userRepository.findByEmail("missing@example.com");
        assertThat(found).isEmpty();
    }
}

