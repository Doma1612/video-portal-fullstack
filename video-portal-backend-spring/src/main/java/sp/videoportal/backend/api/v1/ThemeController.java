package sp.videoportal.backend.api.v1;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sp.videoportal.backend.api.v1.request.ThemeRequest;
import sp.videoportal.backend.dto.ThemaDto;
import sp.videoportal.backend.service.VideoService;

@RestController
@RequestMapping("/api/v1/themes")
public class ThemeController {

    private final VideoService videoService;

    public ThemeController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public List<ThemaDto> listThemes() {
        return videoService.ladeAlleThemen();
    }

    @PostMapping
    public ResponseEntity<ThemaDto> createTheme(@Valid @RequestBody ThemeRequest request) {
        ThemaDto created = videoService.themaAnlegen(request.name());
        return ResponseEntity.created(URI.create("/api/v1/themes/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ThemaDto updateTheme(@PathVariable("id") long id, @Valid @RequestBody ThemeRequest request) {
        return videoService.themaUpdate(id, request.name());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable("id") long id) {
        videoService.themaLoeschen(id);
        return ResponseEntity.noContent().build();
    }
}
