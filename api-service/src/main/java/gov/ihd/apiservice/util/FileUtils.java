package gov.ihd.apiservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public class FileUtils {

    /**
     * Saves a multipart file to the given directory
     *
     * @param file The multipart file to save
     * @param uploadDir The directory to save the file to
     * @return The path to the saved file
     * @throws IOException If an error occurs during file saving
     */
    public static File saveFile(MultipartFile file, String uploadDir) throws IOException {
        // Create the upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("Created upload directory: {}", uploadPath.toAbsolutePath());
        }
        
        // Generate the file path
        String filename = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        
        // Check if file already exists
        if (Files.exists(filePath)) {
            throw new IOException("File " + filename + " already exists in the upload directory.");
        }
        
        // Save the file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        log.info("Saved file: {} (size: {} bytes)", filePath, file.getSize());
        
        return filePath.toFile();
    }
    
    /**
     * Validates if the file is a JSON file
     *
     * @param file The file to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidJsonFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return filename != null && filename.toLowerCase().endsWith(".json");
    }
}
