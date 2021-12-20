package com.onlym.file.storage.service;

import com.onlym.file.storage.properties.StorageProperties;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
class FileSystemStorageServiceTest {

    public static final String FILENAME = "file.txt";
    public static final String CONTENT_TYPE = "file.txt";
    public static final String FILE_CONTENT = "this is an example of txt test file";

    @Autowired
    private FileSystemStorageService storageService;

    @Autowired
    private StorageProperties properties;

    @Test
    void store() throws IOException {
        storeTestFile();
        String pathString = getPathString();
        File f = new File(pathString);
        assertTrue(f.exists());
        String actualFileContent = Files.readString(Path.of(pathString));
        assertEquals(FILE_CONTENT, actualFileContent);
        deleteDir();
    }

    @Test
    void loadAllFilenamesInUploadDir() {
        storeTestFile();
        Stream<Path> pathsStream = storageService.loadAllFilenamesInUploadDir();
        AtomicInteger filesCounter = new AtomicInteger();
        pathsStream.forEach(path -> {
            filesCounter.incrementAndGet();
            assertEquals(Path.of(FILENAME), path);
        });
        assertEquals(1, filesCounter.get());
        deleteDir();
    }

    @Test
    void loadPathByFilename() {
    }

    @Test
    void loadFileByFilename() {
    }

    @Test
    void deleteAll() {
    }

    private void storeTestFile() {
        byte[] content = FILE_CONTENT.getBytes(StandardCharsets.UTF_8);
        MultipartFile result = new MockMultipartFile(FILENAME, FILENAME, CONTENT_TYPE, content);
        storageService.store(result);
    }

    private String getPathString() {
        return properties.getLocation() + "/" + FILENAME;
    }

    private void deleteDir() {
        File file = new File(properties.getLocation());
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                assertTrue(f.delete());
            }
        }
        assertTrue(file.delete());
    }
}