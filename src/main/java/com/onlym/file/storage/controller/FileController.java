package com.onlym.file.storage.controller;

import com.onlym.file.storage.common.FileResponse;
import com.onlym.file.storage.service.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * This is an API controller, responsible for uploading and downloading files
 */
@RestController
public class FileController {

    private final StorageService storageService;

    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {

        Resource resource = storageService.loadFileByFilename(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteAllFiles() {
        storageService.deleteAll();
        return ResponseEntity.ok()
                .body("Deleted all files from FS");
    }

    @PostMapping("/upload")
    public FileResponse uploadMultipleFiles(@RequestParam("files") MultipartFile files) {
        return uploadFile(files);
    }

    private FileResponse uploadFile(MultipartFile file) {
        String name = storageService.store(file);
        String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(name)
                .toUriString();

        return new FileResponse(name, downloadUri, file.getContentType(), file.getSize());
    }

}
