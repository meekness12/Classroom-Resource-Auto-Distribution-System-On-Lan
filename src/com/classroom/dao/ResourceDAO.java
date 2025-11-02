package com.classroom.dao;

import com.classroom.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO {

    public List<String> getAllResources() {
        List<String> resources = new ArrayList<>();
        String sql = "SELECT resource_id, title, type FROM resources";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resources.add(rs.getInt("resource_id") + " - " + rs.getString("title") + " (" + rs.getString("type") + ")");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resources;
    }

    public boolean addResource(String title, String type, String filePath, int uploadedBy) {
        String sql = "INSERT INTO resources (title, type, file_path, uploaded_by) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, type);
            ps.setString(3, filePath);
            ps.setInt(4, uploadedBy);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
