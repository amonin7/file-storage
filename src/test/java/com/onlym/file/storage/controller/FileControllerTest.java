package com.onlym.file.storage.controller;

import com.onlym.file.storage.properties.StorageProperties;
import com.onlym.file.storage.service.FileSystemStorageService;
import com.onlym.file.storage.service.StorageService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FileControllerTest {

    private StorageService storageService;

    private StorageProperties storageProperties;

    @BeforeEach
    void beforeAll() {
        this.storageService = new FileSystemStorageService(storageProperties);
        // create the test directory
    }

    @Test
    void downloadFile() {
    }

    @Test
    void deleteAllFiles() {
    }

    @Test
    void uploadMultipleFiles() {
    }

    @AfterAll
    static void afterAll() {
        // delete the test directory
    }
}