package com.classroom.dao;

import com.classroom.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Fetch all users
    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        String sql = "SELECT user_id, username, full_name, role FROM users";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(rs.getInt("user_id") + " - " + rs.getString("username") + " (" + rs.getString("role") + ")");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Add new user
    public boolean addUser(String username, String fullName, String role, String passwordHash) {
        String sql = "INSERT INTO users (username, full_name, role, password_hash) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, fullName);
            ps.setString(3, role);
            ps.setString(4, passwordHash);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Find user by username
    public boolean validateLogin(String username, String passwordHash) {
        String sql = "SELECT COUNT(*) FROM users WHERE username=? AND password_hash=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
