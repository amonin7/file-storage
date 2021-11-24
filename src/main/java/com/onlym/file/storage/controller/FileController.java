package com.onlym.file.storage.controller;

import com.onlym.file.storage.common.FileResponse;
import com.onlym.file.storage.service.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class FileController {

    private final StorageService storageService;

    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listAllFiles(Model model) {
        Stream<Path> filenamesInUploadDir = storageService.loadAllFilenamesInUploadDir();
        Map<Path, String> pathToDownloadUri = filenamesInUploadDir
                .collect(Collectors.toMap(
                        path -> path,
                        path -> ServletUriComponentsBuilder
                                .fromCurrentContextPath()
                                .path("/download/")
                                .path(path.getFileName().toString())
                                .toUriString()));
        model.addAttribute("files", pathToDownloadUri);

        return "mainPage";
    }

    @GetMapping("/download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {

        Resource resource = storageService.loadFileByFilename(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/upload")
    @ResponseBody
    public List<FileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.stream(files)
                .map(this::uploadFile)
                .collect(Collectors.toList());
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
