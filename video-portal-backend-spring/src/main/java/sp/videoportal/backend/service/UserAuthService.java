package sp.videoportal.backend.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sp.videoportal.backend.domain.User;
import sp.videoportal.backend.dto.CredentialsDto;
import sp.videoportal.backend.dto.UserDto;
import sp.videoportal.backend.repository.UserRepository;

@Service
public class UserAuthService {

    private static final Logger log = LoggerFactory.getLogger(UserAuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserDto> login(CredentialsDto credentials) {
        String normalizedUsername = normalizeUsername(credentials.getUsername());
        return userRepository
            .findByUsername(normalizedUsername)
                .filter(u -> passwordEncoder.matches(credentials.getPassword(), u.getPassword()))
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getRole()));
    }

    public Optional<UserDto> me(Long userId) {
        return userRepository
                .findById(userId)
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getRole()));
    }

    @Transactional
    public Optional<UserDto> register(CredentialsDto credentials) {
        String normalizedUsername = normalizeUsername(credentials.getUsername());
        if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            return Optional.empty();
        }
        User user = new User();
        user.setUsername(normalizedUsername);
        user.setPassword(passwordEncoder.encode(credentials.getPassword()));
        user.setRole(0);
        user = userRepository.save(user);

        log.info("User created: id={}, username={}, role={}", user.getId(), user.getUsername(), user.getRole());
        return Optional.of(new UserDto(user.getId(), user.getUsername(), user.getRole()));
    }

    private static String normalizeUsername(String username) {
        if (username == null) {
            return null;
        }
        return username.trim().toLowerCase();
    }
}
