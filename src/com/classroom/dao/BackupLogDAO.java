package com.classroom.dao;

import com.classroom.model.BackupLog;
import com.classroom.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BackupLogDAO {

    public BackupLogDAO() {
        // No need to load driver here anymore
    }

    public boolean insertLog(int userId, String type, String files, String status) {
        String sql = "INSERT INTO backup_logs (user_id, type, files, status, created_at) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setString(3, files);
            ps.setString(4, status);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

public List<BackupLog> getAllLogs() {
    List<BackupLog> logs = new ArrayList<>();
    String sql = "SELECT * FROM backup_logs ORDER BY backup_time DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            logs.add(new BackupLog(
                rs.getInt("backup_id"),
                rs.getInt("performed_by"),
                rs.getString("backup_type"),
                rs.getString("backup_location"),
                rs.getString("status"),
                rs.getTimestamp("backup_time"),
                rs.getString("files")
            ));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return logs;
}
}