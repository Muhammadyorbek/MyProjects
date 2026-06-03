package com.example.movie_ticket.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/movie_ticket_db";
    private static final String USER = "root";
    private static final String PASSWORD = "javohirbek_2007";

    public static Connection getConnection() {
        try{
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    };
}