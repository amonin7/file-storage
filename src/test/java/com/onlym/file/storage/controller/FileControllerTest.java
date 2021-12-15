package com.onlym.file.storage.controller;

import com.onlym.file.storage.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FileControllerTest {

    private final StorageService storageService;

    FileControllerTest(StorageService storageService) {
        this.storageService = storageService;
    }

    @BeforeEach
    void setUp() {
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
}