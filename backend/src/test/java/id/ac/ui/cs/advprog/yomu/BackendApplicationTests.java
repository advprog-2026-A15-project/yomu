package id.ac.ui.cs.advprog.yomu;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        properties = {
                "spring.flyway.enabled=false"
        }
)
@ActiveProfiles("test")
class BackendApplicationTest {
    @Test
    void contextLoads() {
    }
}