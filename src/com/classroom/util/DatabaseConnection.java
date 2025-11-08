package com.classroom.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // LAN MySQL server configuration
    private static final String URL = "jdbc:mysql://192.168.100.150:3306/classroom_db?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USER = "lanuser";
    private static final String PASSWORD = "Java@123";

    // Optional: timeout settings (in seconds)
    private static final int CONNECTION_TIMEOUT = 10;

    static {
        try {
            // Explicitly load MySQL driver (recommended for Connector/J 9.x)
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            DriverManager.setLoginTimeout(CONNECTION_TIMEOUT); // optional timeout
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to MySQL database successfully!");
            return conn;
        } catch (SQLException e) {
            System.err.println("ERROR: Connection failed!");
            e.printStackTrace();
            return null;
        }
    }

    // Utility method to close connection safely
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Database connection closed successfully.");
            } catch (SQLException e) {
                System.err.println("ERROR: Failed to close connection!");
                e.printStackTrace();
            }
        }
    }
}
