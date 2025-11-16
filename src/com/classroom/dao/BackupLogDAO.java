package com.classroom.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.classroom.util.DatabaseConnection; // Your DB connection utility

public class BackupLogDAO {

    /**
     * Insert a backup log
     * @param username Admin username performing backup
     * @param type Backup type (FULL / FILES / DB)
     * @param files Files/DB path saved
     * @param status SUCCESS or FAILED
     */
    public void insertLog(String username, String type, String files, String status) {
        String sql = "INSERT INTO backup_logs (performed_by, backup_type, files, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, type);
            ps.setString(3, files);
            ps.setString(4, status);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
