package sp.videoportal.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sp.videoportal.backend.domain.Thema;

public interface ThemaRepository extends JpaRepository<Thema, Long> {
    Optional<Thema> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
