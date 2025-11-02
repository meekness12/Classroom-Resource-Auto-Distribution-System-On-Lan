package com.classroom.dao;

import com.classroom.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceAccessDAO {

    // Log a resource access by a student
    public boolean logAccess(int studentId, int resourceId) {
        String sql = "INSERT INTO resource_access (student_id, resource_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, resourceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get all resources accessed by a student
    public List<Integer> getResourcesByStudent(int studentId) {
        List<Integer> resourceIds = new ArrayList<>();
        String sql = "SELECT resource_id FROM resource_access WHERE student_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resourceIds.add(rs.getInt("resource_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resourceIds;
    }

    // Get all students who accessed a particular resource
    public List<Integer> getStudentsByResource(int resourceId) {
        List<Integer> studentIds = new ArrayList<>();
        String sql = "SELECT student_id FROM resource_access WHERE resource_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resourceId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                studentIds.add(rs.getInt("student_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentIds;
    }

    // Optional: Delete a resource access log
    public boolean deleteAccessLog(int accessId) {
        String sql = "DELETE FROM resource_access WHERE access_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accessId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
