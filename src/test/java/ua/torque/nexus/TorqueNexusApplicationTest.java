package ua.torque.nexus;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "server.port=0"
})
class TorqueNexusApplicationTest {

    @Test
    void contextLoads() {
        // Тест проходить, якщо Spring контекст завантажився успішно.
    }

    @Test
    void testMainMethod() {
        assertDoesNotThrow(() -> TorqueNexusApplication.main(new String[]{}));
    }
}
