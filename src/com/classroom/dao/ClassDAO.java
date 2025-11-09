package com.classroom.dao;

import com.classroom.util.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class ClassDAO {

    // Returns list of [id, name, created_at]
    public List<String[]> getAllClasses() {
        List<String[]> classes = new ArrayList<>();
        String sql = "SELECT class_id, class_name, created_at FROM classes ORDER BY class_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                classes.add(new String[]{
                    String.valueOf(rs.getInt("class_id")),
                    rs.getString("class_name"),
                    rs.getString("created_at")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
    }

    // Convenience: get ID from class name (returns -1 if not found)
    public int getClassIdByName(String className) {
        String sql = "SELECT class_id FROM classes WHERE class_name = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, className);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("class_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean addClass(String className) {
        String sql = "INSERT INTO classes (class_name, created_at) VALUES (?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, className);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateClass(int classId, String newName) {
        String sql = "UPDATE classes SET class_name = ? WHERE class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setInt(2, classId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Fetch all classes from database
    public List<String[]> fetchAllClasses() {
    List<String[]> classes = new ArrayList<>();
    try {
        Connection con = DatabaseConnection.getConnection();
        String sql = "SELECT class_id, class_name FROM classes";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            classes.add(new String[]{rs.getString("class_id"), rs.getString("class_name")});
        }

        rs.close();
        ps.close();
        con.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return classes;
}




    public boolean deleteClass(int classId) {
        String sql = "DELETE FROM classes WHERE class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
