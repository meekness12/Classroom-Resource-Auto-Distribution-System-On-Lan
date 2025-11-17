// UserLogDAO.java
package com.classroom.dao;

import com.classroom.util.DatabaseConnection;
import com.classroom.model.UserLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// UserLogDAO.java
public class UserLogDAO {
    public List<UserLog> getAllLogs() {
        List<UserLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM user_logs ORDER BY action_time DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                logs.add(new UserLog(
                        rs.getInt("log_id"),
                        rs.getInt("user_id"),
                        rs.getString("action"),
                        rs.getTimestamp("action_time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
}
