package sp.videoportal.backend.service;

import java.io.IOException;
import java.nio.file.Path;

public interface VideoTranscoder {
    void convertToWebm(Path input, Path output) throws IOException, InterruptedException;
}
