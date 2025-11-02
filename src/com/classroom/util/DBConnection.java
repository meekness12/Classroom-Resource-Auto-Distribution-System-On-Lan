package com.classroom.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.Properties;

public class DBConnection {

    private static Connection connection;

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load Oracle JDBC driver
                Class.forName("oracle.jdbc.driver.OracleDriver");

                // Load config.properties
                Properties props = new Properties();
                InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("com/classroom/util/config.properties");
                props.load(input);

                String host = props.getProperty("db.host");
                String port = props.getProperty("db.port");
                String serviceName = props.getProperty("db.serviceName");
                String username = props.getProperty("db.username");
                String password = props.getProperty("db.password");

                String jdbcUrl = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + serviceName;
                connection = DriverManager.getConnection(jdbcUrl, username, password);
                System.out.println("✅ Connected to Oracle DB successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                throw new SQLException("Failed to connect to Oracle DB");
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
