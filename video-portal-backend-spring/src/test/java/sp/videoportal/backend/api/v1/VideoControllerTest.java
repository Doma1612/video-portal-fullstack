package sp.videoportal.backend.api.v1;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import sp.videoportal.backend.domain.Thema;
import sp.videoportal.backend.repository.ThemaRepository;
import sp.videoportal.backend.service.VideoTranscoder;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:videotest;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.h2.console.enabled=false"
})
@AutoConfigureMockMvc
class VideoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ThemaRepository themaRepository;

    static Path initialDir;
    static Path convertedDir;

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        initialDir = tempDir.resolve("initial_upload");
        convertedDir = tempDir.resolve("converted");
        registry.add("app.storage.initial-upload-dir", () -> initialDir.toString());
        registry.add("app.storage.converted-dir", () -> convertedDir.toString());
        registry.add("app.ffmpeg.path", () -> "ffmpeg");
        registry.add("app.admin.enabled", () -> "false");
    }

    long themeId;

    @BeforeEach
    void setupTheme() {
        if (themaRepository.count() == 0) {
            Thema t = new Thema();
            t.setName("Test");
            t = themaRepository.save(t);
            themeId = t.getId();
        } else {
            themeId = themaRepository.findAll().get(0).getId();
        }
    }

    @Test
    void uploadVideoWithCsrfCreatesVideo() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.mp4",
                "video/mp4",
                "fake-video".getBytes());

        mockMvc.perform(multipart("/api/v1/videos")
                        .file(file)
                        .param("title", "My Video")
                        .param("description", "Desc")
                        .param("themeId", String.valueOf(themeId))
                        .param("keywords", "cat,dog")
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.videoId").isNumber())
                .andExpect(jsonPath("$.titel").exists())
                .andExpect(jsonPath("$.thema.id").value((int) themeId));

        // list should include it
        mockMvc.perform(get("/api/v1/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].videoId").isNumber());
    }

    @TestConfiguration
    static class TestTranscoderConfig {
        @Bean
        @Primary
        VideoTranscoder testVideoTranscoder() {
            return (input, output) -> {
                try {
                    Files.createDirectories(output.getParent());
                    // minimal fake conversion: create output file
                    Files.copy(input, output);
                } catch (IOException e) {
                    throw e;
                }
            };
        }
    }
}
