package sp.videoportal.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler csrfRequestHandler = new CsrfTokenRequestAttributeHandler();

        http
                // Use CORS configuration from MVC (CorsConfig)
                .cors(Customizer.withDefaults())
                // Session-based API
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                // CSRF protection for all state-changing requests
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(csrfRequestHandler))
                // Basic authorization rules (minimal for now)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/csrf").permitAll()
                        // allow public reads (can be tightened later)
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        // allow H2 console in dev
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().permitAll()
                )
                // H2 console needs frames; safe for local dev
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                // No login form / basic auth prompt
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }
}
