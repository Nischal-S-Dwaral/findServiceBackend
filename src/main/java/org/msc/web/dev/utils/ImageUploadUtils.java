package org.msc.web.dev.utils;

import org.msc.web.dev.exceptions.InternalServerError;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUploadUtils {

    public static File convertToFile(MultipartFile multipartFile, String fileName) {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        } catch (IOException exception) {
            throw new InternalServerError("Failed to convert the image to file: " + exception.getMessage());
        }
        return tempFile;
    }

    public static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
