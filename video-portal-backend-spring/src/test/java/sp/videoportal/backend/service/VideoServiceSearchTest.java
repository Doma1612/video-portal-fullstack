package sp.videoportal.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sp.videoportal.backend.domain.Thema;
import sp.videoportal.backend.domain.Video;
import sp.videoportal.backend.repository.ThemaRepository;
import sp.videoportal.backend.repository.VideoRepository;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:searchtest;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.h2.console.enabled=false",
        "app.admin.enabled=false"
})
class VideoServiceSearchTest {

    @Autowired
    VideoService videoService;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    ThemaRepository themaRepository;

    @Test
    void searchMatchesTitleDescriptionAndMetadata_caseInsensitive() {
        Thema thema = new Thema();
        thema.setName("T");
        thema = themaRepository.save(thema);

        Video a = new Video();
        a.setTitel("Cats_are_great.mp4");
        a.setBeschreibung("A funny video");
        a.setMetaData("pets,cat");
        a.setThema(thema);
        videoRepository.save(a);

        Video b = new Video();
        b.setTitel("Dogs.mp4");
        b.setBeschreibung("CAT in description");
        b.setMetaData("animals");
        b.setThema(thema);
        videoRepository.save(b);

        Video c = new Video();
        c.setTitel("Birds.mp4");
        c.setBeschreibung("Nothing to see");
        c.setMetaData("nature");
        c.setThema(thema);
        videoRepository.save(c);

        List<?> results = videoService.ladeVideosNachSuche("cat");
        assertThat(results).hasSize(2);

        List<?> results2 = videoService.ladeVideosNachSuche("CAT");
        assertThat(results2).hasSize(2);
    }
}
