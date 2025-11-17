package com.classroom.model;

import java.sql.Timestamp;

public class Announcement {
    private int id;
    private String senderRole;
    private String senderId;
    private String target;
    private String message;
    private Timestamp createdAt;

    public Announcement(int id, String senderRole, String senderId, String target, String message, Timestamp createdAt) {
        this.id = id;
        this.senderRole = senderRole;
        this.senderId = senderId;
        this.target = target;
        this.message = message;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getSenderRole() { return senderRole; }
    public String getSenderId() { return senderId; }
    public String getTarget() { return target; }
    public String getMessage() { return message; }
    public Timestamp getCreatedAt() { return createdAt; }
}
