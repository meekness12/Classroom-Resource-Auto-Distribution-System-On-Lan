package com.classroom.dao;

import com.classroom.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentClassDAO {

    public boolean enrollStudent(int studentId, int classId) {
        String sql = "INSERT INTO student_classes (student_id, class_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, classId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Integer> getClassesOfStudent(int studentId) {
        List<Integer> classIds = new ArrayList<>();
        String sql = "SELECT class_id FROM student_classes WHERE student_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                classIds.add(rs.getInt("class_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classIds;
    }
}
