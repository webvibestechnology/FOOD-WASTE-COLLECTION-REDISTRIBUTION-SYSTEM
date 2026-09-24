package com.food_waste_backend.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

    private static final String UPLOAD_DIRECTORY = "uploads";

    public static String saveFile(MultipartFile file, String subDirectory)
            throws IOException {

        Path uploadPath = Paths.get(UPLOAD_DIRECTORY, subDirectory);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IOException("Invalid file name");
        }

        String uniqueFilename =
                UUID.randomUUID() + "_" + originalFilename;

        Path filePath = uploadPath.resolve(uniqueFilename);

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        return UPLOAD_DIRECTORY + "/" + subDirectory + "/" + uniqueFilename;
    }
}