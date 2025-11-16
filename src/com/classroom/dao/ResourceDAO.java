package com.classroom.dao;

import com.classroom.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO {

    // Add a new resource
    public boolean addResource(String title, String type, String filePath, int uploadedBy) {
        String sql = "INSERT INTO resources (title, type, file_path, uploaded_by) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, type);
            ps.setString(3, filePath);
            ps.setInt(4, uploadedBy);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a resource by ID
    public boolean deleteResource(int resourceId) {
        String sql = "DELETE FROM resources WHERE resource_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, resourceId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all resources (for admin view)
    public List<String[]> getAllResources() {
        List<String[]> resources = new ArrayList<>();
        String sql = "SELECT resource_id, title, type, file_path, uploaded_at FROM resources ORDER BY uploaded_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resources.add(new String[]{
                        String.valueOf(rs.getInt("resource_id")),
                        rs.getString("title"),
                        rs.getString("type"),
                        rs.getString("file_path"),
                        rs.getString("uploaded_at")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resources;
    }

    // Get all resources uploaded by a specific lecturer
    public List<String[]> getAllResourcesByLecturer(String lecturerId) {
        List<String[]> resources = new ArrayList<>();
        String sql = "SELECT resource_id, title, type, file_path FROM resources WHERE uploaded_by = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(lecturerId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resources.add(new String[]{
                            String.valueOf(rs.getInt("resource_id")),
                            rs.getString("title"),
                            rs.getString("type"),
                            rs.getString("file_path")
                    });
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resources;
    }

    // Get file path of a resource by ID
    public String getFilePathById(int resourceId) {
        String sql = "SELECT file_path FROM resources WHERE resource_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, resourceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("file_path");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
