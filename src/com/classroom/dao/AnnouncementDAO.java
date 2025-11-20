package com.classroom.dao;

import com.classroom.model.Announcement;
import com.classroom.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAO {

    // INSERT ANNOUNCEMENT
    public boolean sendAnnouncement(String senderRole, String senderId, String target, String message) {
        String sql = "INSERT INTO announcements (sender_role, sender_id, target, message) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, senderRole);
            stmt.setInt(2, Integer.parseInt(senderId));
            stmt.setString(3, target);
            stmt.setString(4, message);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<String[]> getAllAnnouncements() {
    List<String[]> announcements = new ArrayList<>();
    String sql = "SELECT announcement_id, target, message, created_at FROM announcements ORDER BY created_at DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            String[] row = new String[4];
            row[0] = String.valueOf(rs.getInt("announcement_id"));
            row[1] = rs.getString("target");
            row[2] = rs.getString("message");
            row[3] = rs.getString("created_at");
            announcements.add(row);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return announcements;
}

    // FETCH ANNOUNCEMENTS FOR A LECTURER
    public List<String[]> getAnnouncementsByLecturer(int lecturerId) {
        List<String[]> list = new ArrayList<>();

        String sql = """
            SELECT announcement_id, message, target, created_at
            FROM announcements
            WHERE sender_id = ?
            ORDER BY created_at DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lecturerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new String[] {
                        rs.getString("announcement_id"),
                        rs.getString("message"),
                        rs.getString("target"),
                        rs.getString("created_at")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // FETCH FOR STUDENTS (ONLY TARGETED TO STUDENTS)
    public List<Announcement> getAnnouncementsForStudent() {
        List<Announcement> list = new ArrayList<>();
        String sql = "SELECT * FROM announcements " +
                     "WHERE target = 'ALL' OR target = 'STUDENTS' " +
                     "ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Announcement(
                        rs.getInt("announcement_id"),
                        rs.getString("sender_role"),
                        rs.getString("sender_id"),
                        rs.getString("target"),
                        rs.getString("message"),
                        rs.getTimestamp("created_at")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
