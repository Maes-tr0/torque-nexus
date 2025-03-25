package ua.torque.nexus.feature.token.email.model;

import org.junit.jupiter.api.Test;
import ua.torque.nexus.user.model.User;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ConfirmationTokenTest {

    @Test
    void setIdShouldBePrivate() throws Exception {
        Method setter = ConfirmationToken.class.getDeclaredMethod("setId", Long.class);
        assertThat(Modifier.isPrivate(setter.getModifiers())).isTrue();

        // verify reflection can still set it
        setter.setAccessible(true);
        ConfirmationToken u = new ConfirmationToken();
        setter.invoke(u, 123L);
        assertThat(u.getId()).isEqualTo(123L);
    }

    @Test
    void builderShouldSetFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        ConfirmationToken token = ConfirmationToken.builder()
                .token("abc123")
                .createdAt(now)
                .expiresAt(now.plusHours(1))
                .user(user)
                .build();

        token.setConfirmedAt(now.plusMinutes(30));

        assertThat(token.getToken()).isEqualTo("abc123");
        assertThat(token.getCreatedAt()).isEqualTo(now);
        assertThat(token.getExpiresAt()).isEqualTo(now.plusHours(1));
        assertThat(token.getConfirmedAt()).isEqualTo(now.plusMinutes(30));
        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.getId()).isNull();
    }

    @Test
    void noArgsConstructorAndSettersShouldWork() {
        ConfirmationToken token = new ConfirmationToken();
        token.setToken("def456");
        LocalDateTime now = LocalDateTime.now();
        token.setCreatedAt(now);
        token.setExpiresAt(now.plusDays(1));
        token.setConfirmedAt(now.plusHours(2));
        User user = new User();
        token.setUser(user);
        assertThat(token.getToken()).isEqualTo("def456");
        assertThat(token.getCreatedAt()).isEqualTo(now);
        assertThat(token.getExpiresAt()).isEqualTo(now.plusDays(1));
        assertThat(token.getConfirmedAt()).isEqualTo(now.plusHours(2));
        assertThat(token.getUser()).isEqualTo(user);
    }

    @Test
    void equalsAndHashCodeShouldBeBasedOnAllFields() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        ConfirmationToken token1 = ConfirmationToken.builder()
                .token("ghi789")
                .createdAt(now)
                .expiresAt(now.plusHours(2))
                .user(user)
                .build();

        token1.setConfirmedAt(now.plusMinutes(45));

        ConfirmationToken token2 = ConfirmationToken.builder()
                .token("ghi789")
                .createdAt(now)
                .expiresAt(now.plusHours(2))
                .user(user)
                .build();

        token2.setConfirmedAt(now.plusMinutes(45));

        assertThat(token1).isEqualTo(token2).hasSameHashCodeAs(token2);
        ConfirmationToken token3 = ConfirmationToken.builder()
                .token("different")
                .createdAt(now)
                .expiresAt(now.plusHours(2))
                .user(user)
                .build();

        token3.setConfirmedAt(now.plusMinutes(45));

        assertThat(token1).isNotEqualTo(token3);
    }
}
