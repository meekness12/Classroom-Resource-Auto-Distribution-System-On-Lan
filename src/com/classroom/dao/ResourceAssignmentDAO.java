package com.classroom.dao;

import com.classroom.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ResourceAssignmentDAO {

    // ✅ Updated to accept 3 parameters (resourceId, classId, lecturerId)
    public boolean assignResourceToClass(int resourceId, int classId, int lecturerId) {
        String sql = "INSERT INTO resource_assignments (resource_id, class_id, lecturer_id, assigned_on) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, resourceId);
            ps.setInt(2, classId);
            ps.setInt(3, lecturerId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
