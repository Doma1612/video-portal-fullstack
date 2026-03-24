package sp.videoportal.backend.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sp.videoportal.backend.config.VideoProcessingProperties;
import sp.videoportal.backend.domain.Thema;
import sp.videoportal.backend.domain.Unterkategorie;
import sp.videoportal.backend.domain.Video;
import sp.videoportal.backend.dto.ThemaDto;
import sp.videoportal.backend.dto.UnterkategorieDto;
import sp.videoportal.backend.dto.VideoDto;
import sp.videoportal.backend.repository.ThemaRepository;
import sp.videoportal.backend.repository.UnterkategorieRepository;
import sp.videoportal.backend.repository.VideoRepository;

@Service
public class VideoService {

    private final ThemaRepository themaRepository;
    private final UnterkategorieRepository unterkategorieRepository;
    private final VideoRepository videoRepository;
    private final VideoProcessingProperties properties;
    private final VideoTranscoder transcoder;

    public VideoService(
            ThemaRepository themaRepository,
            UnterkategorieRepository unterkategorieRepository,
            VideoRepository videoRepository,
            VideoProcessingProperties properties,
            VideoTranscoder transcoder) {
        this.themaRepository = themaRepository;
        this.unterkategorieRepository = unterkategorieRepository;
        this.videoRepository = videoRepository;
        this.properties = properties;
        this.transcoder = transcoder;
    }

    public List<ThemaDto> ladeAlleThemen() {
        return themaRepository.findAll().stream()
                .map(t -> new ThemaDto(t.getId(), t.getName()))
                .toList();
    }

    @Transactional
    public ThemaDto themaAnlegen(String name) {
        String normalizedName = requireText(name, "name");
        if (themaRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new ConflictException("Category already exists");
        }
        Thema t = new Thema();
        t.setName(normalizedName);
        t = themaRepository.save(t);
        return new ThemaDto(t.getId(), t.getName());
    }

    @Transactional
    public ThemaDto themaUpdate(long id, String name) {
        String normalizedName = requireText(name, "name");
        if (themaRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, id)) {
            throw new ConflictException("Category already exists");
        }
        Thema thema = themaRepository.findById(id).orElseThrow();
        thema.setName(normalizedName);
        thema = themaRepository.save(thema);
        return new ThemaDto(thema.getId(), thema.getName());
    }

    @Transactional
    public void themaLoeschen(long id) {
        themaRepository.deleteById(id);
    }

    public List<UnterkategorieDto> ladeAlleUnterkategorien() {
        return unterkategorieRepository.findAll().stream()
                .map(uk -> new UnterkategorieDto(
                        uk.getId(),
                        uk.getName(),
                        new ThemaDto(uk.getThema().getId(), uk.getThema().getName())))
                .toList();
    }

    @Transactional
    public UnterkategorieDto uKategorieAnlegen(String name, long themeId) {
        Thema thema = themaRepository.findById(themeId).orElseThrow();
        Unterkategorie uk = new Unterkategorie();
        uk.setName(requireText(name, "name"));
        uk.setThema(thema);
        uk = unterkategorieRepository.save(uk);
        return new UnterkategorieDto(
                uk.getId(),
                uk.getName(),
                new ThemaDto(thema.getId(), thema.getName()));
    }

    @Transactional
    public UnterkategorieDto uKategorieUpdate(long id, String name, long themeId) {
        Unterkategorie uk = unterkategorieRepository.findById(id).orElseThrow();
        uk.setName(requireText(name, "name"));
        Thema thema = themaRepository.findById(themeId).orElseThrow();
        uk.setThema(thema);
        uk = unterkategorieRepository.save(uk);
        return new UnterkategorieDto(
                uk.getId(),
                uk.getName(),
                new ThemaDto(thema.getId(), thema.getName()));
    }

    @Transactional
    public void uKategorieLoeschen(long id) {
        unterkategorieRepository.deleteById(id);
    }

    @Transactional
    public VideoDto videoHinzufuegen(
            String title,
            String description,
            long themeId,
            String keywords,
            Collection<Long> subcategoryIds,
            String originalFilename,
            InputStream videoStream) throws IOException {

        Thema thema = themaRepository.findById(themeId).orElseThrow();

        String safeTitle = sanitizeTitle(requireText(title, "title"));
        String extension = extractExtension(originalFilename);
        String storedTitel = extension.isBlank() ? safeTitle : safeTitle + "." + extension;

        Video video = new Video();
        video.setTitel(storedTitel);
        video.setBeschreibung(requireText(description, "description"));
        video.setMetaData(keywords);
        video.setThema(thema);
        video.setUnterKategorien(subcategoryIds == null ? List.of() : List.copyOf(subcategoryIds));
        video.setAufrufZaehler(0);

        video = videoRepository.saveAndFlush(video);

        Path inputFilePath = buildInputFilePath(video.getVideoId(), storedTitel);
        Path outputFilePath = buildOutputFilePath(video.getVideoId(), storedTitel, extension);

        try {
            Files.createDirectories(inputFilePath.getParent());
            Files.createDirectories(outputFilePath.getParent());

            Files.copy(videoStream, inputFilePath, StandardCopyOption.REPLACE_EXISTING);
            transcoder.convertToWebm(inputFilePath, outputFilePath);

            video.setDateipfad(outputFilePath.toString());
            video = videoRepository.save(video);
            return toDto(video);
        } catch (Exception e) {
            safeDeleteFile(inputFilePath);
            safeDeleteFile(outputFilePath);
            videoRepository.deleteById(video.getVideoId());
            if (e instanceof IOException ioe) {
                throw ioe;
            }
            throw new IOException("Upload/Conversion failed", e);
        }
    }

    @Transactional
    public Path ladeVideoPfad(long id, boolean incrementView) {
        Video video = videoRepository.findById(id).orElseThrow();
        if (video.getDateipfad() == null || video.getDateipfad().isBlank()) {
            throw new IllegalStateException("Video has no stored file path");
        }
        Path path = Paths.get(video.getDateipfad());
        if (!Files.isRegularFile(path)) {
            throw new NoSuchElementException("Video file not found");
        }
        if (incrementView) {
            video.incrementAufrufZaehler();
            videoRepository.save(video);
        }
        return path;
    }

    @Transactional(readOnly = true)
    public VideoDto ladeVideoMeta(long id) {
        Video video = videoRepository.findById(id).orElseThrow();
        return toDto(video);
    }

    @Transactional(readOnly = true)
    public List<VideoDto> ladeAlleVideos() {
        return videoRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<VideoDto> ladeVideosNachSuche(String stichwoerter) {
        List<String> keywords = splitKeywords(stichwoerter);
        return videoRepository.findAll().stream()
                .filter(v -> matchesAnyKeyword(v, keywords))
                .map(this::toDto)
                .toList();
    }

    private boolean matchesAnyKeyword(Video v, List<String> keywords) {
        String meta = Optional.ofNullable(v.getMetaData()).orElse("").toLowerCase(Locale.ROOT);
        String desc = Optional.ofNullable(v.getBeschreibung()).orElse("").toLowerCase(Locale.ROOT);
        String title = Optional.ofNullable(v.getTitel()).orElse("").toLowerCase(Locale.ROOT);
        for (String k : keywords) {
            String kk = k.toLowerCase(Locale.ROOT);
            if (meta.contains(kk) || desc.contains(kk) || title.contains(kk)) {
                return true;
            }
        }
        return keywords.isEmpty();
    }

    private List<String> splitKeywords(String stichwoerter) {
        if (stichwoerter == null || stichwoerter.isBlank()) {
            return List.of();
        }
        return List.of(stichwoerter.split(",\\s*"));
    }

    private String extractExtension(String filename) {
        String ext = StringUtils.getFilenameExtension(filename);
        if (ext == null) {
            return "";
        }
        return ext.trim().toLowerCase(Locale.ROOT);
    }

    private String sanitizeTitle(String title) {
        String sanitized = title.trim().replace(" ", "_");
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        while (sanitized.contains("__")) {
            sanitized = sanitized.replace("__", "_");
        }
        return sanitized;
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.trim();
    }

    private Path buildInputFilePath(Long videoId, String storedTitel) {
        return Paths.get(properties.getStorage().getInitialUploadDir())
                .resolve(String.valueOf(videoId))
                .resolve(storedTitel);
    }

    private Path buildOutputFilePath(Long videoId, String storedTitel, String inputExtension) {
        String outName = storedTitel;
        if (inputExtension != null && !inputExtension.isBlank()) {
            outName = storedTitel.replace("." + inputExtension, ".webm");
        }
        if (!outName.endsWith(".webm")) {
            outName = outName + ".webm";
        }
        return Paths.get(properties.getStorage().getConvertedDir())
                .resolve(String.valueOf(videoId))
                .resolve(outName);
    }

    private void safeDeleteFile(Path path) {
        if (path == null) {
            return;
        }
        try {
            Files.deleteIfExists(path);
        } catch (Exception ignored) {
        }
    }

    private VideoDto toDto(Video v) {
        VideoDto dto = new VideoDto();
        dto.setVideoId(v.getVideoId());
        dto.setTitel(v.getTitel());
        dto.setBeschreibung(v.getBeschreibung());
        dto.setMetaData(v.getMetaData());
        dto.setAnzahlAufrufe(v.getAufrufZaehler());
        dto.setUnterKategorien(v.getUnterKategorien() == null ? List.of() : List.copyOf(v.getUnterKategorien()));
        dto.setThema(new ThemaDto(v.getThema().getId(), v.getThema().getName()));
        dto.setName(toDisplayName(v.getTitel()));
        return dto;
    }

    private String toDisplayName(String storedTitel) {
        if (storedTitel == null) {
            return "";
        }
        String name = storedTitel.replace("_", " ");
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0) {
            name = name.substring(0, lastDot);
        }
        return name;
    }
}
