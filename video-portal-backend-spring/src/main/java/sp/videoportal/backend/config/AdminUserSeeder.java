package sp.videoportal.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sp.videoportal.backend.domain.User;
import sp.videoportal.backend.repository.UserRepository;

@Component
public class AdminUserSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserSeeder.class);

    private final Environment environment;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserSeeder(Environment environment, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.environment = environment;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        boolean enabled = environment.getProperty("app.admin.enabled", Boolean.class, true);
        if (!enabled) {
            return;
        }

        String username = environment.getProperty("app.admin.username");
        String password = environment.getProperty("app.admin.password");
        int role = environment.getProperty("app.admin.role", Integer.class, 1);

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            // Not configured – do nothing.
            return;
        }

        final String normalizedUsername = username.trim().toLowerCase();

        userRepository.findByUsernameIgnoreCase(normalizedUsername).ifPresentOrElse(existing -> {
            boolean changed = false;

            if (existing.getRole() != role) {
                existing.setRole(role);
                changed = true;
            }

            boolean updatePassword = environment.getProperty("app.admin.update-password", Boolean.class, false);
            if (updatePassword) {
                existing.setPassword(passwordEncoder.encode(password));
                changed = true;
            }

            if (changed) {
                userRepository.save(existing);
                log.info("Admin seed updated: id={}, username={}, role={}", existing.getId(), existing.getUsername(), existing.getRole());
            } else {
                log.info("Admin seed exists: id={}, username={}, role={}", existing.getId(), existing.getUsername(), existing.getRole());
            }
        }, () -> {
            User admin = new User();
            admin.setUsername(normalizedUsername);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(role);
            admin = userRepository.save(admin);
            log.info("Admin seed created: id={}, username={}, role={}", admin.getId(), admin.getUsername(), admin.getRole());
        });
    }
}
