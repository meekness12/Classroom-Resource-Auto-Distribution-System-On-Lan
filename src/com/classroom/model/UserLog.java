// UserLog.java
package com.classroom.model;

import java.sql.Timestamp;

public class UserLog {
    private int logId;
    private int userId;
    private String action;
    private Timestamp actionTime;

    public UserLog(int logId, int userId, String action, Timestamp actionTime) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.actionTime = actionTime;
    }

    // Getters
    public int getLogId() { return logId; }
    public int getUserId() { return userId; }
    public String getAction() { return action; }
    public Timestamp getActionTime() { return actionTime; }
}
