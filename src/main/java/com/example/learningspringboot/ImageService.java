package com.example.learningspringboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ImageService {

    private static String UPLOAD_ROOT = "upload-dir";

    private final ImageRepository imageRepository;

    private final ResourceLoader resourceLoader;

    @Autowired
    public ImageService(ImageRepository imageRepository, ResourceLoader resourceLoader) {
        this.imageRepository = imageRepository;
        this.resourceLoader = resourceLoader;
    }

    public Resource findOneImage(String filename) {
        return resourceLoader.getResource("file://" + UPLOAD_ROOT + "/" + filename);
    }

    public void createImage(MultipartFile file) throws IOException {
        if(!file.isEmpty()) {
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));
            imageRepository.save(new Image(file.getOriginalFilename()));
        }
    }

    public void deleteImage(String filename) throws IOException {
        final Image byName = imageRepository.findByName(filename);
        imageRepository.delete(byName);
        Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
    }

    @Bean
    CommandLineRunner setUp(ImageRepository imageRepository) throws IOException {
        return (args) -> {
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));
            Files.createDirectory(Paths.get(UPLOAD_ROOT));
            FileCopyUtils.copy("Test file 1", new FileWriter(UPLOAD_ROOT + "/test1"));
            imageRepository.save(new Image("test1"));

            FileCopyUtils.copy("Test file 2", new FileWriter(UPLOAD_ROOT + "/test2"));
            imageRepository.save(new Image("test2"));

            FileCopyUtils.copy("Test file 3", new FileWriter(UPLOAD_ROOT + "/test3"));
            imageRepository.save(new Image("test3"));
        };
    }
}
