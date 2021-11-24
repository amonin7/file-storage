package com.onlym.file.storage.controller;

import com.onlym.file.storage.service.StorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class MainController {

    private final StorageService storageService;

    public MainController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String main(Model model) {
        model.addAttribute("files", getPathToDownloadUri());

        return "mainPage";
    }

    @PostMapping("/")
    public String uploadFile(Map<String, Object> model,
                             @RequestParam("files") MultipartFile[] files) {
        Arrays.stream(files).forEach(storageService::store);
        model.put("files", getPathToDownloadUri());

        return "mainPage";
    }

    private Map<Path, String> getPathToDownloadUri() {
        Stream<Path> filenamesInUploadDir = storageService.loadAllFilenamesInUploadDir();
        return filenamesInUploadDir
                .collect(Collectors.toMap(
                        path -> path,
                        path -> ServletUriComponentsBuilder
                                .fromCurrentContextPath()
                                .path("/download/")
                                .path(path.getFileName().toString())
                                .toUriString()));
    }
}
