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
import sp.videoportal.backend.api.v1.request.SubcategoryRequest;
import sp.videoportal.backend.dto.UnterkategorieDto;
import sp.videoportal.backend.service.VideoService;

@RestController
@RequestMapping("/api/v1/subcategories")
public class SubcategoryController {

    private final VideoService videoService;

    public SubcategoryController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public List<UnterkategorieDto> listSubcategories() {
        return videoService.ladeAlleUnterkategorien();
    }

    @PostMapping
    public ResponseEntity<UnterkategorieDto> createSubcategory(
            @Valid @RequestBody SubcategoryRequest request) {
        UnterkategorieDto created = videoService.uKategorieAnlegen(request.name(), request.themeId());
        return ResponseEntity.created(
                        URI.create("/api/v1/subcategories/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public UnterkategorieDto updateSubcategory(
            @PathVariable("id") long id, @Valid @RequestBody SubcategoryRequest request) {
        return videoService.uKategorieUpdate(id, request.name(), request.themeId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubcategory(@PathVariable("id") long id) {
        videoService.uKategorieLoeschen(id);
        return ResponseEntity.noContent().build();
    }
}
