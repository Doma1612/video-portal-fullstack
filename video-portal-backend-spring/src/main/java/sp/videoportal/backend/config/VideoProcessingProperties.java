package sp.videoportal.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class VideoProcessingProperties {

    private final Ffmpeg ffmpeg = new Ffmpeg();
    private final Storage storage = new Storage();

    public Ffmpeg getFfmpeg() {
        return ffmpeg;
    }

    public Storage getStorage() {
        return storage;
    }

    public static class Ffmpeg {
        private String path = "ffmpeg";

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class Storage {
        private String initialUploadDir = "./data/initial_upload";
        private String convertedDir = "./data/converted";

        public String getInitialUploadDir() {
            return initialUploadDir;
        }

        public void setInitialUploadDir(String initialUploadDir) {
            this.initialUploadDir = initialUploadDir;
        }

        public String getConvertedDir() {
            return convertedDir;
        }

        public void setConvertedDir(String convertedDir) {
            this.convertedDir = convertedDir;
        }
    }
}
