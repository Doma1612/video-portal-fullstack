package sp.videoportal.backend.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import org.springframework.stereotype.Component;
import sp.videoportal.backend.config.VideoProcessingProperties;

@Component
public class FfmpegVideoTranscoder implements VideoTranscoder {

    private final VideoProcessingProperties properties;

    public FfmpegVideoTranscoder(VideoProcessingProperties properties) {
        this.properties = properties;
    }

    @Override
    public void convertToWebm(Path input, Path output) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                properties.getFfmpeg().getPath(),
                "-y",
                "-i",
                input.toAbsolutePath().toString(),
                "-map",
                "0:v:0",
                "-map",
                "0:a?",
                "-vf",
                "scale='min(1280,iw)':-2",
                "-c:v",
                "libvpx",
                "-b:v",
                "1M",
                "-crf",
                "32",
                "-deadline",
                "realtime",
                "-cpu-used",
                "4",
                "-threads",
                "2",
                "-c:a",
                "libopus",
                "-b:a",
                "96k",
                output.toAbsolutePath().toString());
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try (var in = p.getInputStream()) {
            in.transferTo(OutputStream.nullOutputStream());
        }
        int exit = p.waitFor();
        if (exit != 0) {
            throw new IOException("ffmpeg failed with exit code " + exit);
        }
    }
}
