package com.classroom.model;

import java.sql.Timestamp;

public class Resource {
    private int resourceId;
    private String title;
    private String type;
    private String filePath;
    private int uploadedBy;
    private Timestamp uploadedOn;

    public Resource(int resourceId, String title, String type, String filePath, int uploadedBy, Timestamp uploadedOn) {
        this.resourceId = resourceId;
        this.title = title;
        this.type = type;
        this.filePath = filePath;
        this.uploadedBy = uploadedBy;
        this.uploadedOn = uploadedOn;
    }

    public int getResourceId() { return resourceId; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getFilePath() { return filePath; }
    public int getUploadedBy() { return uploadedBy; }
    public Timestamp getUploadedOn() { return uploadedOn; }
}
