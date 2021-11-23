package io.blindet.file.storage.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    String store(MultipartFile file);

    Stream<Path> loadAllFilenamesInUploadDir();

    Path loadPathByFilename(String filename);

    Resource loadFileByFilename(String filename);

    void deleteAll();

}
