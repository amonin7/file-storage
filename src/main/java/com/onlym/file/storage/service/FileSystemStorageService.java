package com.onlym.file.storage.service;

import com.onlym.file.storage.properties.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {
    private final Path uploadLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.uploadLocation = Paths.get(properties.getLocation());
    }

    @Override
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(uploadLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file.getOriginalFilename() == null) {
            throw new StorageException("Uploaded file has empty filename. Failed to store it.");
        }
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        if (file.isEmpty()) {
            throw new StorageException("Uploaded file(\"" + filename + "\") is empty. Failed to store it.");
        }
        try {
            if (filename.contains("..")) {
                // this means that the file relative path will leave the directory it should be placed
                throw new StorageException(
                        "Filename \"" + filename + "\" contains the restricted symbols \"..\"");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream,
                        this.uploadLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file(\"" + filename + "\")", e);
        }

        return filename;
    }

    @Override
    public Stream<Path> loadAllFilenamesInUploadDir() {
        try {
            return Files.walk(this.uploadLocation, 1)
                    .filter(path -> !path.equals(this.uploadLocation))
                    .map(this.uploadLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("The error occurred while reading stored files", e);
        }
    }

    @Override
    public Path loadPathByFilename(String filename) {
        return uploadLocation.resolve(filename);
    }

    @Override
    public Resource loadFileByFilename(String filename) {
        try {
            Path file = loadPathByFilename(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new FileNotFoundException("Failed to read file(\"" + filename + "\")");
            }
        }
        catch (MalformedURLException e) {
            throw new FileNotFoundException("Failed to read file(\"" + filename + "\")", e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(uploadLocation.toFile());
    }
}
