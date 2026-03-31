package com.queenstouch.queenstouchbackend.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.queenstouch.queenstouchbackend.config.AppProperties;
import com.queenstouch.queenstouchbackend.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCloudStorageService {

    private final AppProperties appProperties;
    private Storage storage;

    private void initStorage() {
        if (storage != null) return;
        
        AppProperties.GoogleCloudStorage config = appProperties.getGoogleCloudStorage();
        if (config.getProjectId() == null || config.getBucket() == null || config.getCredentialsBase64() == null) {
            throw AppException.internalError("Google Cloud Storage configuration is missing");
        }

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(config.getCredentialsBase64());
            try (InputStream in = new ByteArrayInputStream(decodedBytes)) {
                GoogleCredentials credentials = GoogleCredentials.fromStream(in);
                storage = StorageOptions.newBuilder()
                        .setProjectId(config.getProjectId())
                        .setCredentials(credentials)
                        .build()
                        .getService();
            }
        } catch (IOException e) {
            log.error("Failed to initialize Google Cloud Storage client: {}", e.getMessage(), e);
            throw AppException.internalError("Failed to initialize cloud storage");
        }
    }

    public String uploadFile(MultipartFile file) {
        initStorage();
        String bucketName = appProperties.getGoogleCloudStorage().getBucket();
        
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        try {
            storage.create(blobInfo, file.getBytes());
            log.info("File uploaded to GCS bucket {} as {}", bucketName, fileName);
            return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
        } catch (IOException e) {
            log.error("Failed to upload file to Google Cloud Storage: {}", e.getMessage(), e);
            throw AppException.internalError("Failed to upload file to cloud storage");
        }
    }
}
