package cinema.booking.cinemabooking.service;

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

@Service
public class FileStorageService {

    private final Path uploadPath;
    private final List<String> allowedExtensions;

    // Wstrzykujemy wartości z application.properties, ale dajemy wartości domyślne (po dwukropku)
    public FileStorageService(
            @Value("${app.upload.dir:uploads}") String uploadDir,
            @Value("${app.upload.allowed-extensions:jpg,jpeg,png,webp}") String extensions) {

        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.allowedExtensions = Arrays.asList(extensions.split(","));

        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException ex) {
            throw new RuntimeException("Nie można utworzyć katalogu dla plików", ex);
        }
    }

    public String storeFile(MultipartFile file, String movieTitle) {
        // 1. Walidacja czy plik nie jest pusty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Nie można zapisać pustego pliku");
        }

        // 2. Pobranie oryginalnej nazwy
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Nazwa pliku jest wymagana");
        }

        // 3. Sprawdzenie rozszerzenia
        String extension = getFileExtension(originalFilename);
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Niedozwolone rozszerzenie pliku (" + extension + "). Dozwolone: " + allowedExtensions);
        }

        // 4. Generowanie unikalnej nazwy pliku (Tytuł_filmu_UUID.rozszerzenie)
        String filename = generateUniqueFilename(movieTitle, extension);
        Path targetLocation = this.uploadPath.resolve(filename);

        try {
            // 5. Zapis pliku na dysk
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // ZWRACAMY ŚCIEŻKĘ RELATYWNĄ DO UŻYCIA W HTML
            // (WebConfig mapuje /uploads/** na ten folder)
            return "/uploads/" + filename;

        } catch (IOException ex) {
            throw new RuntimeException("Błąd podczas zapisu pliku", ex);
        }
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;

        try {
            // Wyciągamy samą nazwę pliku z URL (usuwamy "/uploads/")
            String filename = fileUrl.replace("/uploads/", "");
            Path filePath = uploadPath.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            // Logujemy błąd, ale nie przerywamy działania aplikacji
            System.err.println("Nie udało się usunąć pliku: " + ex.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return filename.substring(lastDot + 1);
    }

    private String generateUniqueFilename(String movieTitle, String extension) {
        // Jeśli nie podano tytułu, użyj "movie"
        String safeTitle = (movieTitle != null && !movieTitle.isEmpty()) ? movieTitle : "movie";

        // Czyszczenie tytułu z niedozwolonych znaków (zostawiamy tylko litery i cyfry)
        String cleanTitle = safeTitle.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

        // Dodanie UUID dla unikalności (skrócone do 8 znaków dla czytelności)
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);

        return cleanTitle + "_" + uniqueId + "." + extension;
    }
}