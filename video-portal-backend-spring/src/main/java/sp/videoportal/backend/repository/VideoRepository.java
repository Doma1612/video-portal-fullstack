package sp.videoportal.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sp.videoportal.backend.domain.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {}
