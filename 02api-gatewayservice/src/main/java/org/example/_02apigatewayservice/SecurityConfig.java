package org.example._02apigatewayservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import javax.crypto.spec.SecretKeySpec;

@Configuration
    @EnableWebFluxSecurity
    public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                // 1. Disable CSRF for Postman/External API calls
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // 2. Configure Authorization rules
                .authorizeExchange(exchanges -> exchanges
                        // Allow registration and login without a token
                        .pathMatchers("/auth/register", "/auth/login", "/auth/**").permitAll()

                        // Protect everything else (including your file uploads)
                        .anyExchange().authenticated()
                )

                // 3. Enable JWT validation at the Gateway
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() { // Changed to Reactive
        String secret = "ThisIsASecretKeyThatIsExactlySixtyFourCharactersLongToFixTheError";
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");

        // Use NimbusReactiveJwtDecoder instead of NimbusJwtDecoder
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();
    }
}