package com.example;

import java.sql.*;

/**
 *
 * @author johe2765 Jonathan Heikel (Wening)
 */
public class DBConnection {

    public Connection connect() {
        String url = "jdbc:mysql://localhost:3306/bekn5739";
        String username = "bekn5739";
        String password = "iw8seeCh8ag9";

        System.out.println("Connecting database...");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Database connected!");
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }
    
    public void disconnect() {
        // 
    }

}
