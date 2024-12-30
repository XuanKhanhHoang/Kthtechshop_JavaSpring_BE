package com.kth.kthtechshop.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.client.http.InputStreamContent;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.logging.Logger;

@Service
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final Logger LOGGER = Logger.getLogger(GoogleDriveService.class.getName());

    @Value("${google.service.account.key}")
    private String serviceAccountKey;

    @Value("${google.drive.folder.id}")
    private String folderId;

    public Drive getDriveService() throws GeneralSecurityException, IOException {
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(serviceAccountKey))
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/drive.file"));

        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public String uploadFile(MultipartFile file) throws IOException, GeneralSecurityException {
        String originalFilename = file.getOriginalFilename();
        return uploadFile(file, originalFilename);
    }

    public String uploadFile(MultipartFile file, String fileName) throws IOException, GeneralSecurityException {
        Drive driveService = getDriveService();
        File fileMetadata = new File();
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) throw new RuntimeException();
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        if (!(extension.endsWith(".png") || extension.endsWith(".jpg") || extension.endsWith(".webp")))
            throw new BadRequestException();
        fileMetadata.setName(originalFilename.contains(".") ? fileName : (fileName + extension));
        fileMetadata.setParents(Collections.singletonList(folderId));
        InputStream inputStream = file.getInputStream();
        InputStreamContent mediaContent = new InputStreamContent(file.getContentType(), inputStream);
        File uploadedFile = driveService.files().create(fileMetadata, mediaContent).setFields("id").execute();
        LOGGER.info("File uploaded successfully with ID: " + uploadedFile.getId());
        return uploadedFile.getId();
    }
}
