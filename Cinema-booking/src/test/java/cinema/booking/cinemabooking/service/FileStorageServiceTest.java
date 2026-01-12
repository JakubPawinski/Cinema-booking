package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.exception.FileStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @TempDir
    Path uploadDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService(
                uploadDir.toString(),
                "jpg,jpeg,png,webp"
        );
    }

    @Test
    void testStoreFileSuccessfully() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        // Act
        String result = fileStorageService.storeFile(file, "Inception");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).startsWith("/uploads/");
        assertThat(result).contains("Inception");
        assertThat(result).endsWith(".jpg");
    }

    @Test
    void testStoreFileWithPngExtension() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "test content".getBytes()
        );

        // Act
        String result = fileStorageService.storeFile(file, "Avatar");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).startsWith("/uploads/");
        assertThat(result).endsWith(".png");
    }

    @Test
    void testStoreFileWithWebpExtension() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.webp",
                "image/webp",
                "test content".getBytes()
        );

        // Act
        String result = fileStorageService.storeFile(file, "Dune");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).endsWith(".webp");
    }

    @Test
    void testStoreFileWithEmptyFile() {
        // Arrange
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        // Act & Assert
        assertThatThrownBy(() -> fileStorageService.storeFile(emptyFile, "Inception"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Could not store empty file");
    }

    @Test
    void testStoreFileWithInvalidExtension() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes()
        );

        // Act & Assert
        assertThatThrownBy(() -> fileStorageService.storeFile(file, "Inception"))
                .isInstanceOf(FileStorageException.class)
                .hasMessageContaining("Invalid file extension");
    }

    @Test
    void testStoreFileWithUppercaseExtension() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.JPG",
                "image/jpeg",
                "test content".getBytes()
        );

        // Act
        String result = fileStorageService.storeFile(file, "Inception");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).startsWith("/uploads/");
    }

    @Test
    void testStoreFileWithLongMovieTitle() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );
        String longTitle = "The Lord of the Rings: The Fellowship of the Ring Extended Edition";

        // Act
        String result = fileStorageService.storeFile(file, longTitle);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).startsWith("/uploads/");
        assertThat(result).doesNotContain(" ");
    }

    @Test
    void testStoreFileWithSpecialCharactersInTitle() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );
        String titleWithSpecialChars = "Movie!@#$%^&*()";

        // Act
        String result = fileStorageService.storeFile(file, titleWithSpecialChars);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).startsWith("/uploads/");
        assertThat(result).doesNotContain("!@#$%^&*()");
    }

    @Test
    void testStoreFileWithNullOriginalFilename() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                null,
                "image/jpeg",
                "test content".getBytes()
        );

        // Act & Assert
        assertThatThrownBy(() -> fileStorageService.storeFile(file, "Inception"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Original filename is invalid");
    }

    @Test
    void testStoreFileWithEmptyMovieTitle() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        // Act
        String result = fileStorageService.storeFile(file, "");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).startsWith("/uploads/");
        assertThat(result).contains("movie");
    }

    @Test
    void testStoreMultipleFiles() {
        // Arrange
        MultipartFile file1 = new MockMultipartFile(
                "file",
                "test1.jpg",
                "image/jpeg",
                "content1".getBytes()
        );
        MultipartFile file2 = new MockMultipartFile(
                "file",
                "test2.png",
                "image/png",
                "content2".getBytes()
        );

        // Act
        String result1 = fileStorageService.storeFile(file1, "Inception");
        String result2 = fileStorageService.storeFile(file2, "Avatar");

        // Assert
        assertThat(result1).isNotEqualTo(result2);
        assertThat(result1).contains("Inception");
        assertThat(result2).contains("Avatar");
    }

    @Test
    void testDeleteFileSuccessfully() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );
        String fileUrl = fileStorageService.storeFile(file, "Inception");

        // Act
        fileStorageService.deleteFile(fileUrl);

        // Assert
        String filename = fileUrl.replace("/uploads/", "");
        Path filePath = uploadDir.resolve(filename);
        assertThat(Files.exists(filePath)).isFalse();
    }

    @Test
    void testDeleteFileWithNullUrl() {
        // Act & Assert - should not throw exception
        assertThatNoException().isThrownBy(() -> fileStorageService.deleteFile(null));
    }

    @Test
    void testDeleteFileWithEmptyUrl() {
        // Act & Assert - should not throw exception
        assertThatNoException().isThrownBy(() -> fileStorageService.deleteFile(""));
    }

    @Test
    void testDeleteNonExistentFile() {
        // Act & Assert - should not throw exception
        assertThatNoException().isThrownBy(() -> fileStorageService.deleteFile("/uploads/nonexistent.jpg"));
    }

    @Test
    void testStoreFileCreatesFileOnDisk() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        // Act
        String fileUrl = fileStorageService.storeFile(file, "Inception");

        // Assert
        String filename = fileUrl.replace("/uploads/", "");
        Path filePath = uploadDir.resolve(filename);
        assertThat(Files.exists(filePath)).isTrue();
        assertThat(Files.size(filePath)).isGreaterThan(0);
    }

    @Test
    void testStoreFileWithDifferentContentTypes() {
        // Arrange
        MultipartFile jpegFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );
        MultipartFile pngFile = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "test content".getBytes()
        );

        // Act
        String jpegResult = fileStorageService.storeFile(jpegFile, "Movie1");
        String pngResult = fileStorageService.storeFile(pngFile, "Movie2");

        // Assert
        assertThat(jpegResult).endsWith(".jpg");
        assertThat(pngResult).endsWith(".png");
    }

    @Test
    void testStoreFileGeneratesUniqueFilenames() {
        // Arrange
        MultipartFile file1 = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "content1".getBytes()
        );
        MultipartFile file2 = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "content2".getBytes()
        );

        // Act
        String result1 = fileStorageService.storeFile(file1, "SameMovie");
        String result2 = fileStorageService.storeFile(file2, "SameMovie");

        // Assert
        assertThat(result1).isNotEqualTo(result2);
    }

    @Test
    void testDeleteFileAndStoreNewFileWithSameName() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );
        String fileUrl = fileStorageService.storeFile(file, "Inception");
        fileStorageService.deleteFile(fileUrl);

        // Act
        MultipartFile newFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "new content".getBytes()
        );
        String newFileUrl = fileStorageService.storeFile(newFile, "Inception");

        // Assert
        assertThat(newFileUrl).isNotNull();
        String newFilename = newFileUrl.replace("/uploads/", "");
        Path newFilePath = uploadDir.resolve(newFilename);
        assertThat(Files.exists(newFilePath)).isTrue();
    }

    @Test
    void testStoreFileWithJpegExtension() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpeg",
                "image/jpeg",
                "test content".getBytes()
        );

        // Act
        String result = fileStorageService.storeFile(file, "Movie");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).endsWith(".jpeg");
    }

    @Test
    void testConstructorWithInvalidUploadDirectory() {
        // Act & Assert
        assertThatThrownBy(() -> new FileStorageService(
                "/invalid/path/that/cannot/be/created/uploads",
                "jpg,jpeg,png,webp"
        ))
                .isInstanceOf(FileStorageException.class)
                .hasMessageContaining("Could not create upload directory");
    }

    @Test
    void testStoreFileWithIOException() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        ) {
            @Override
            public java.io.InputStream getInputStream() throws IOException {
                throw new IOException("Simulated IO error");
            }
        };

        // Act & Assert
        assertThatThrownBy(() -> fileStorageService.storeFile(file, "Inception"))
                .isInstanceOf(FileStorageException.class)
                .hasMessageContaining("Error storing file")
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void testDeleteFileWithIOException() throws IOException {
        // Arrange
        String fileUrl = "/uploads/Inception_12345678.jpg";
        Path filePath = uploadDir.resolve("Inception_12345678.jpg");

        // Create the file
        Files.createFile(filePath);

        // Make parent directory read-only to simulate permission error
        Files.setAttribute(uploadDir, "posix:permissions",
                java.nio.file.attribute.PosixFilePermissions.fromString("r-xr-xr-x"));

        // Act & Assert - should not throw exception, only log error
        assertThatNoException().isThrownBy(() -> fileStorageService.deleteFile(fileUrl));

        // Cleanup
        Files.setAttribute(uploadDir, "posix:permissions",
                java.nio.file.attribute.PosixFilePermissions.fromString("rwxr-xr-x"));
    }

}
