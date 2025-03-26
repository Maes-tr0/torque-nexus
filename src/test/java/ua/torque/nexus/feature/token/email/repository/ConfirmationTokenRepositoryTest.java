package ua.torque.nexus.feature.token.email.repository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import ua.torque.nexus.access.model.Role;
import ua.torque.nexus.access.model.RoleType;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;
import ua.torque.nexus.user.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "FLYWAY_URL=jdbc:h2:mem:testdb",
        "spring.flyway.enabled=false"
})
class ConfirmationTokenRepositoryTest {

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EntityManager entityManager;

    private Role createRole(RoleType type) {
        Role existing = entityManager
                .createQuery("SELECT r FROM Role r WHERE r.type = :type", Role.class)
                .setParameter("type", type)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (existing != null) {
            return existing;
        }

        Role role = Role.builder()
                .type(type)
                .permissions(new HashSet<>())
                .build();
        entityManager.persist(role);
        return role;
    }


    private User createUser(String email) {
        Role role = createRole(RoleType.CUSTOMER);
        User user = new User();
        user.setEmail(email);
        user.setFullName("John Doe");
        user.setPassword("Password1234");
        user.setEmailConfirmed(false);
        user.setRole(role);
        entityManager.persist(user);
        return user;
    }

//    @Test
//    void testFindAllByExpiresAtBefore_returnsExpiredTokens() {
//        LocalDateTime now = LocalDateTime.now();
//        User user1 = createUser("user1@example.com");
//        User user2 = createUser("user2@example.com");
//        User user3 = createUser("user3@example.com");
//        ConfirmationToken ct1 = ConfirmationToken.builder()
//                .token("token1")
//                .createdAt(now.minusHours(2))
//                .expiresAt(now.minusMinutes(5))
//                .user(user1)
//                .build();
//        ConfirmationToken ct2 = ConfirmationToken.builder()
//                .token("token2")
//                .createdAt(now.minusHours(1))
//                .expiresAt(now.minusMinutes(1))
//                .user(user2)
//                .build();
//        ConfirmationToken ct3 = ConfirmationToken.builder()
//                .token("token3")
//                .createdAt(now)
//                .expiresAt(now.plusHours(1))
//                .user(user3)
//                .build();
//        confirmationTokenRepository.save(ct1);
//        confirmationTokenRepository.save(ct2);
//        confirmationTokenRepository.save(ct3);
//        entityManager.flush();
//        entityManager.clear();
//        var expiredTokens = confirmationTokenRepository.findAllByExpiresAtBefore(now);
//        assertThat(expiredTokens).hasSize(2);
//        assertThat(expiredTokens).extracting(ConfirmationToken::getToken)
//                .containsExactlyInAnyOrder("token1", "token2");
//    }

    @Test
    void testFindByUser_returnsToken() {
        User user = createUser("user4@example.com");
        ConfirmationToken token = ConfirmationToken.builder()
                .token("unique-token")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(1))
                .user(user)
                .build();
        confirmationTokenRepository.save(token);
        entityManager.flush();
        entityManager.clear();
        User managedUser = entityManager.find(User.class, user.getId());
        ConfirmationToken found = confirmationTokenRepository.findByUser(managedUser);
        assertThat(found).isNotNull();
        assertThat(found.getToken()).isEqualTo("unique-token");
    }

    @Test
    void testFindByToken_returnsToken() {
        User user = createUser("user5@example.com");
        ConfirmationToken token = ConfirmationToken.builder()
                .token("search-token")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(2))
                .user(user)
                .build();
        confirmationTokenRepository.save(token);
        entityManager.flush();
        entityManager.clear();
        Optional<ConfirmationToken> result = confirmationTokenRepository.findByToken("search-token");
        assertThat(result).isPresent();
        assertThat(result.get().getUser().getEmail()).isEqualTo("user5@example.com");
    }

    @Test
    void testFindByToken_returnsEmptyIfNotFound() {
        Optional<ConfirmationToken> result = confirmationTokenRepository.findByToken("nonexistent-token");
        assertThat(result).isNotPresent();
    }
}
