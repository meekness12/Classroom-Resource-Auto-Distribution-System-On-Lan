package com.classroom.util;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class GoogleDriveBackup {

    private static final String APPLICATION_NAME = "Classroom Resources Backup";
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/drive.file");
    private static final java.io.File TOKENS_DIRECTORY = new java.io.File("tokens");

    public GoogleDriveBackup() {}

    public static Drive getDriveService() throws Exception {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                GsonFactory.getDefaultInstance(),
                new InputStreamReader(new FileInputStream("credentials.json"))
        );

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                clientSecrets,
                SCOPES
        )
        .setDataStoreFactory(new FileDataStoreFactory(TOKENS_DIRECTORY)) // Save credentials here
        .setAccessType("offline") // ensures refresh token is saved
        .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
        )
        .setApplicationName(APPLICATION_NAME)
        .build();
    }
    

public static String getOrCreateFolder(Drive driveService, String folderName) throws Exception {
    // Check if folder already exists
    String query = "mimeType='application/vnd.google-apps.folder' and name='" + folderName + "' and trashed=false";
    FileList result = driveService.files().list()
            .setQ(query)
            .setFields("files(id, name)")
            .execute();

    if (!result.getFiles().isEmpty()) {
        return result.getFiles().get(0).getId(); // folder exists
    }

    // Create folder
    File folderMetadata = new File();
    folderMetadata.setName(folderName);
    folderMetadata.setMimeType("application/vnd.google-apps.folder");

    File folder = driveService.files().create(folderMetadata)
            .setFields("id")
            .execute();
    System.out.println("Created folder: " + folderName + " (ID: " + folder.getId() + ")");
    return folder.getId();
}


    
    public static String uploadFileToFolder(String filePath, String fileName, String folderId) {
    try {
        Drive driveService = getDriveService();

        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        fileMetadata.setParents(Collections.singletonList(folderId)); // Upload inside folder

        java.io.File file = new java.io.File(filePath);
        FileContent mediaContent = new FileContent("application/octet-stream", file);

        File uploadedFile = driveService.files()
                .create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        System.out.println("Uploaded to folder: " + uploadedFile.getId());
        return uploadedFile.getId();
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

}
