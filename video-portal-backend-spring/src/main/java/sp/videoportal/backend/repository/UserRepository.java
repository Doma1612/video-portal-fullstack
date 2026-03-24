package sp.videoportal.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sp.videoportal.backend.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);
}
