package id.ac.ui.cs.advprog.yomu.authentication.config;

import id.ac.ui.cs.advprog.yomu.authentication.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withBean(JwtAuthenticationFilter.class, () -> mock(JwtAuthenticationFilter.class))
                    .withBean(CustomUserDetailsService.class, () -> mock(CustomUserDetailsService.class))
                    .withUserConfiguration(SecurityConfig.class);

    @Test
    void securityFilterChain_ShouldBuildSuccessfully() {
        contextRunner.run(context -> {
            SecurityFilterChain chain = context.getBean(SecurityFilterChain.class);
            assertThat(chain).isNotNull();
        });
    }
}