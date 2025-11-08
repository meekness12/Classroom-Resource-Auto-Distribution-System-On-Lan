package com.classroom.util;

import java.sql.Connection;

public class TestDBConnection {
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            System.out.println("Connection test succeeded!");
            DatabaseConnection.closeConnection(conn);
        } else {
            System.out.println("Connection test failed!");
        }
    }
}
