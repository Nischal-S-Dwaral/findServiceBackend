package org.msc.web.dev.repository;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.msc.web.dev.constants.ServiceConstants;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.utils.ImageUploadUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Upload images to firebase storage
 */
@Component
@Slf4j
public class ServiceImageUploadRepository {

    private Storage storage;

    @PostConstruct
    public void init() {
        try {
            File configFile = ResourceUtils.getFile("classpath:config/findservice-firebase-adminsdk.json");

            FileInputStream serviceAccount =
                    new FileInputStream(configFile);

            storage = StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build()
                    .getService();
        } catch (Exception exception) {
            log.error("Failed to initialise connection to FireBase Storage: "+exception.getMessage());
        }
    }

    public List<String> uploadServiceImageToFirebase(List<MultipartFile> multipartFileList) {

        List<String> uploadServiceImageUrlList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFileList) {
            //Generate random name for image
            String fileName = UUID.randomUUID().toString()
                    .concat(ImageUploadUtils.getExtension(Objects.requireNonNull(multipartFile.getOriginalFilename())));
            File file = ImageUploadUtils.convertToFile(multipartFile, fileName);
            try{
                BlobId blobId = BlobId.of(ServiceConstants.SERVICE_IMAGE_BUCKET_NAME, fileName);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
                storage.create(blobInfo, Files.readAllBytes(file.toPath()));
                file.delete();

                //Add the uploaded URL to the list
                uploadServiceImageUrlList.add(String.format(
                        ServiceConstants.FIREBASE_STORAGE_IMAGE_URL_FORMAT_STRING,
                        URLEncoder.encode(fileName, StandardCharsets.UTF_8)));

            } catch (IOException exception) {
                throw new InternalServerError("Failed to upload image: "+multipartFile.getOriginalFilename());
            }
        }
        return uploadServiceImageUrlList;
    }
}
