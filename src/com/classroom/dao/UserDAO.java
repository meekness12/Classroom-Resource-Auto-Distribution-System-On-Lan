package com.classroom.dao;

import com.classroom.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Fetch all users
    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        String sql = "SELECT user_id, username, full_name, role FROM users ORDER BY user_id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("user_id");
                String username = rs.getString("username");
                String fullName = rs.getString("full_name");
                String role = rs.getString("role");

                users.add(id + " - " + username + " - " + fullName + " (" + role + ")");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching users:");
            e.printStackTrace();
        }
        return users;
    }

    // Add new user
    public boolean addUser(String username, String fullName, String role, String passwordHash) {
        String sql = "INSERT INTO users (username, full_name, role, password_hash) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, fullName);
            ps.setString(3, role);
            ps.setString(4, passwordHash);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding user:");
            e.printStackTrace();
        }
        return false;
    }

    // Validate login credentials
    public boolean validateLogin(String username, String passwordHash) {
        String sql = "SELECT COUNT(*) FROM users WHERE username=? AND password_hash=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error validating login:");
            e.printStackTrace();
        }
        return false;
    }

    // Fetch all students
    public List<String[]> getAllStudents() {
        List<String[]> students = new ArrayList<>();
        String sql = "SELECT user_id, username, full_name FROM users WHERE role='STUDENT'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String[] student = new String[3];
                student[0] = String.valueOf(rs.getInt("user_id"));
                student[1] = rs.getString("username");
                student[2] = rs.getString("full_name");
                students.add(student);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    // Fetch user by ID
    public String getUserById(int userId) {
        String sql = "SELECT username, full_name, role FROM users WHERE user_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username") + " - " + rs.getString("full_name") + " (" + rs.getString("role") + ")";
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user by ID:");
            e.printStackTrace();
        }
        return null;
    }

    // Update user info
    public boolean updateUser(int userId, String fullName, String role, String passwordHash) {
        String sql = "UPDATE users SET full_name=?, role=?, password_hash=? WHERE user_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, role);
            ps.setString(3, passwordHash);
            ps.setInt(4, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating user:");
            e.printStackTrace();
        }
        return false;
    }

    // Delete user by ID
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting user:");
            e.printStackTrace();
        }
        return false;
    }

    // Get lecturer ID by username
    public int getLecturerIdByUsername(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ------------------ NEW METHOD ------------------
    // Get student class by username
    public String getStudentClass(String username) {
        String sql = "SELECT c.class_name " +
                     "FROM student_classes sc " +
                     "JOIN classes c ON sc.class_id = c.class_id " +
                     "JOIN users u ON sc.student_id = u.user_id " +
                     "WHERE u.username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("class_name");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching student class:");
            e.printStackTrace();
        }
        return null;
    }
    // USE AnnouncementDAO INTERNALLY
AnnouncementDAO announcementDAO = new AnnouncementDAO();

public boolean sendAnnouncement(String senderRole, String senderId, String target, String message) {
    return announcementDAO.sendAnnouncement(senderRole, senderId, target, message);
}

public List<String[]> getAllAnnouncementsByLecturer(int lecturerId) {
    return announcementDAO.getAnnouncementsByLecturer(lecturerId);
}
// In UserDAO.java
public List<String[]> getAllAnnouncements() {
    return announcementDAO.getAllAnnouncements(); // Make sure AnnouncementDAO has this method
}



}
