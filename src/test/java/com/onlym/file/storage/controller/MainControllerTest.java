package com.onlym.file.storage.controller;

import com.onlym.file.storage.properties.StorageProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StorageProperties properties;

    @Test
    public void contextLoads() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Simple server file storage")));
    }

    @Test
    void main() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Simple server file storage")))
                .andExpect(content().string(containsString("No files put in storage yet")));
    }

    @Test
    void uploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "test-file.txt",
                "text/plain", "Some dataset...".getBytes(StandardCharsets.UTF_8));
        MockMultipartHttpServletRequestBuilder multipart = multipart("/")
                .file(file);

        this.mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Simple server file storage")))
                .andExpect(content().string(containsString("Files in storage")))
                .andExpect(xpath("//*[@id='files-table']/tr").nodeCount(1));
    }

    @AfterEach
    void deleteTestUploadDir() {
        File uploadTestDir = Paths.get(properties.getLocation()).toFile();
        if (uploadTestDir.exists()) {
            File[] contents = uploadTestDir.listFiles();
            if (contents != null) {
                for (File f : contents) {
                    f.delete();
                }
            }
            uploadTestDir.delete();
        }
    }
}