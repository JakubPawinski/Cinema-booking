package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service for handling file storage operations such as saving and deleting files.
 */
@Service
@Slf4j
public class FileStorageService {

    private final Path uploadPath;
    private final List<String> allowedExtensions;

    /**
     * Constructor to initialize the file storage service with configuration values.
     *
     * @param uploadDir         Directory where files will be uploaded.
     * @param extensions        Comma-separated list of allowed file extensions.
     */
    public FileStorageService(
            @Value("${app.upload.dir:uploads}") String uploadDir,
            @Value("${app.upload.allowed-extensions:jpg,jpeg,png,webp}") String extensions) {

        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.allowedExtensions = Arrays.asList(extensions.split(","));

        try {
            Files.createDirectories(this.uploadPath);
            log.info("Initialized file storage at {}", this.uploadPath.toString());
        } catch (IOException ex) {
            log.error("Could not create upload directory", ex);
            throw new FileStorageException("Could not create upload directory", ex);
        }
    }

    /**
     * Stores a file on the server.
     *
     * @param file        The file to be stored.
     * @param movieTitle  The title of the movie associated with the file.
     * @return The relative URL path to access the stored file.
     */
    public String storeFile(MultipartFile file, String movieTitle) {
        log.info("Attempting to store file for movie: {}", movieTitle);

        // Check if file is empty
        if (file.isEmpty()) {
            log.warn("Upload attempt failed: File is empty");
            throw new IllegalArgumentException("Could not store empty file");
        }

        // Get original filename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            log.warn("Upload attempt failed: Original filename is null");
            throw new IllegalArgumentException("Original filename is invalid");
        }

        // Check file extension
        String extension = getFileExtension(originalFilename);
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            log.warn("Upload attempt failed: Invalid extension '{}' for file '{}'", extension, originalFilename);
            throw new FileStorageException(
                    "Invalid file extension (" + extension + "). Allowed: " + allowedExtensions);
        }

        // Generate unique filename
        String filename = generateUniqueFilename(movieTitle, extension);
        Path targetLocation = this.uploadPath.resolve(filename);

        try {
            log.debug("Copying file to {}", targetLocation.toString());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored successfully: {}", filename);
            return "/uploads/" + filename;

        } catch (IOException ex) {
            log.error("Error storing file '{}'", filename, ex);
            throw new FileStorageException("Error storing file", ex);
        }
    }

    /**
     * Deletes a file from the server.
     *
     * @param fileUrl The relative URL path of the file to be deleted.
     */
    public void deleteFile(String fileUrl) {
        // Check if fileUrl is null or empty
        if (fileUrl == null || fileUrl.isEmpty()) {
            log.warn("Delete attempt failed: File URL is null or empty");
            return;
        };

        try {
            String filename = fileUrl.replace("/uploads/", "");
            Path filePath = uploadPath.resolve(filename).normalize();

            if (Files.deleteIfExists(filePath)) {
                log.info("File deleted successfully: {}", filename);
            } else {
                log.warn("File not found for deletion: {}", filename);
            }
        } catch (IOException ex) {
            log.error("Error deleting file: {}", fileUrl, ex);
        }
    }

    /**
     * Extracts the file extension from a filename.
     *
     * @param filename The filename to extract the extension from.
     * @return The file extension, or an empty string if none found.
     */
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return (lastDot == -1) ? "" : filename.substring(lastDot + 1);
    }

    /**
     * Generates a unique filename using the movie title and a UUID.
     *
     * @param movieTitle The title of the movie.
     * @param extension  The file extension.
     * @return A unique filename.
     */
    private String generateUniqueFilename(String movieTitle, String extension) {
        String safeTitle = (movieTitle != null && !movieTitle.isEmpty())
                ? movieTitle.replaceAll("[^a-zA-Z0-9.\\-]", "_")
                : "movie";

        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return safeTitle + "_" + uniqueId + "." + extension;
    }
}