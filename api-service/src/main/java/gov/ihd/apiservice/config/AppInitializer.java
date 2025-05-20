package gov.ihd.apiservice.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Component
public class AppInitializer {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    /**
     * Initialize necessary application directories on startup
     */
    @PostConstruct
    public void init() {
        try {
            // Create upload directory if it doesn't exist
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                Files.createDirectories(Paths.get(uploadDir));
                log.info("Created upload directory: {}", uploadDirectory.getAbsolutePath());
            } else {
                log.info("Upload directory already exists: {}", uploadDirectory.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("Error creating application directories: {}", e.getMessage(), e);
        }
    }
}
