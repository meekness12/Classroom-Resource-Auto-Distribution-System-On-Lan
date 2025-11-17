package com.classroom.model;

import java.sql.Timestamp;

public class BackupLog {
    private int id;
    private int userId;            // performed_by
    private String type;           // backup_type
    private String location;       // backup_location
    private String status;
    private Timestamp backupTime;
    private String files;          // files (optional)

    public BackupLog(int id, int userId, String type, String location, String status, Timestamp backupTime, String files) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.location = location;
        this.status = status;
        this.backupTime = backupTime;
        this.files = files;
    }

    // getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getType() { return type; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
    public Timestamp getBackupTime() { return backupTime; }
    public String getFiles() { return files; }
}
