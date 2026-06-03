package com.example.movie_ticket.database;


import com.example.movie_ticket.model.Cinema;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CinemaDAO {

    public List<com.example.movie_ticket.model.Cinema> getCinemasByCity(String cityName) {
        List<com.example.movie_ticket.model.Cinema> cinemas = new ArrayList<>();
        String sql = "SELECT * FROM cinemas WHERE city = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cityName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                cinemas.add(new com.example.movie_ticket.model.Cinema(
                        rs.getInt("cinema_id"),
                        rs.getString("name"),
                        rs.getString("city")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cinemas;
    }

    public void addCinema(String name, String cityName) {
        String sql = "INSERT INTO cinemas (name, city) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, cityName);
            pstmt.executeUpdate();
            System.out.println("Кинотеатр добавлен в БД!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCinema(String name) {
        String sql = "DELETE FROM cinemas WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Cinema> getAllCinemas() {
        List<Cinema> cinemas = new ArrayList<>();
        String sql = "SELECT * FROM cinemas";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cinemas.add(new Cinema(rs.getInt("cinema_id"), rs.getString("name"), rs.getString("city")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cinemas;
    }
}