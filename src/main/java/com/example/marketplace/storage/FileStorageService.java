package com.example.marketplace.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root;

    public FileStorageService(@Value("${app.storage.dir}") String dir) throws IOException {
        this.root = Path.of(dir).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    /** Сохраняет файл под случайным именем, оригинальное имя пользователя в путь не попадает. */
    public String store(MultipartFile file, Set<String> allowedExtensions) {
        String ext = extensionOf(file.getOriginalFilename());
        if (!allowedExtensions.contains(ext)) {
            throw new IllegalArgumentException(
                    "Недопустимый формат «" + ext + "». Разрешены: " + String.join(", ", allowedExtensions));
        }
        String storedName = UUID.randomUUID() + "." + ext;
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, root.resolve(storedName));
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось сохранить файл", e);
        }
        return storedName;
    }

    public Resource load(String storedName) {
        Path path = root.resolve(storedName).normalize();
        try {
            Resource resource = new UrlResource(path.toUri());
            if (!path.startsWith(root) || !resource.exists()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Файл не найден");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Файл не найден");
        }
    }

    private static String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
