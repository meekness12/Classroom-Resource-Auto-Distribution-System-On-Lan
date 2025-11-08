package com.classroom.dao;

import com.classroom.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Fetch all users
    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        String sql = "SELECT user_id, username, full_name, role FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(rs.getInt("user_id") + " - " + rs.getString("username") + " (" + rs.getString("role") + ")");
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
}
