package sp.videoportal.backend.api.v1;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpRange;
import sp.videoportal.backend.dto.VideoDto;
import sp.videoportal.backend.service.VideoService;

@Validated
@RestController
@RequestMapping("/api/v1/videos")
public class VideoController {

    private static final long DEFAULT_CHUNK_SIZE = 1024 * 1024; // 1MB

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public List<VideoDto> listVideos(@RequestParam(name = "q", required = false) String query) {
        if (query == null || query.isBlank()) {
            return videoService.ladeAlleVideos();
        }
        return videoService.ladeVideosNachSuche(query);
    }

    @GetMapping("/{id}")
    public VideoDto getVideo(@PathVariable("id") long id) {
        return videoService.ladeVideoMeta(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VideoDto> uploadVideo(
            @RequestPart("file") MultipartFile file,
            @RequestParam("title") @NotBlank String title,
            @RequestParam("description") @NotBlank String description,
            @RequestParam("themeId") @NotNull Long themeId,
            @RequestParam(value = "keywords", required = false) String keywords,
            @RequestParam(value = "subcategoryIds", required = false) List<Long> subcategoryIds)
            throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("file is required");
        }

        String originalFilename = file.getOriginalFilename();
        Collection<Long> subIds = (subcategoryIds == null) ? List.of() : List.copyOf(subcategoryIds);

        VideoDto created;
        try (var in = file.getInputStream()) {
            created =
                videoService.videoHinzufuegen(
                    title,
                    description,
                    themeId,
                    keywords,
                    subIds,
                    originalFilename,
                    in);
        }

        return ResponseEntity.status(201)
                .header(HttpHeaders.LOCATION, "/api/v1/videos/" + created.getVideoId())
                .body(created);
    }

    @GetMapping(value = "/{id}/stream", produces = "video/webm")
    public ResponseEntity<ResourceRegion> streamVideo(
            @PathVariable("id") long id, @RequestHeader HttpHeaders headers) throws IOException {

        List<HttpRange> ranges = headers.getRange();
        boolean shouldCountView = ranges.isEmpty() || ranges.get(0).getRangeStart(Long.MAX_VALUE) == 0;

        Path path = videoService.ladeVideoPfad(id, shouldCountView);
        Resource video = new FileSystemResource(path);
        long contentLength = video.contentLength();

        if (ranges.isEmpty()) {
            ResourceRegion region = new ResourceRegion(video, 0, Math.min(DEFAULT_CHUNK_SIZE, contentLength));
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaType.parseMediaType("video/webm"))
                    .body(region);
        }

        HttpRange range = ranges.get(0);
        long start = range.getRangeStart(contentLength);
        long end = range.getRangeEnd(contentLength);
        long rangeLength = Math.min(DEFAULT_CHUNK_SIZE, end - start + 1);

        ResourceRegion region = new ResourceRegion(video, start, rangeLength);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaType.parseMediaType("video/webm"))
                .body(region);
    }
}
