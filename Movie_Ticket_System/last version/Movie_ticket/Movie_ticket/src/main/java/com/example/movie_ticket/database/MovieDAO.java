package com.example.movie_ticket.database;

import com.example.movie_ticket.model.AdminMovieRow;
import com.example.movie_ticket.model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;

public class MovieDAO {

    public void addMovie(String title, String genre, String language, int duration,
                         int releaseYear, String rating, double price, String imageUrl, String description) {
        String sql = "INSERT INTO movies (title, genre, language, duration, release_year, rating, price, image_url, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, genre);
            ps.setString(3, language);
            ps.setInt(4, duration);
            ps.setInt(5, releaseYear);
            ps.setString(6, rating);
            ps.setDouble(7, price);
            ps.setString(8, imageUrl);
            ps.setString(9, description);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void DeleteMovie(Integer id) {
        String sql = "DELETE FROM movies WHERE movie_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Movie deleted!");
            else System.out.println("Movie not found!");
        } catch (SQLException e) { e.printStackTrace(); }

    }

    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                movies.add(new Movie(
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getString("language"),
                        rs.getInt("duration"),
                        rs.getInt("release_year"),
                        rs.getString("rating"),
                        rs.getDouble("price"),
                        rs.getString("image_url"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return movies;
    }

    public List<AdminMovieRow> getAllAdminMovies() {
        List<AdminMovieRow> movies = new ArrayList<>();
        String sql = "SELECT movie_id, title, release_year, rating, genre FROM movies";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                movies.add(new AdminMovieRow(
                        String.valueOf(rs.getInt("movie_id")),
                        rs.getString("title"),
                        String.valueOf(rs.getInt("release_year")),
                        rs.getString("rating"),
                        rs.getString("genre")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return movies;
    }

    public void updateMovie(int id, String title, String genre, String language, int duration,
                            int releaseYear, String rating, double price, String imageUrl, String description) {
        String sql = "UPDATE movies SET title=?, genre=?, language=?, duration=?, release_year=?, rating=?, price=?, image_url=?, description=? WHERE movie_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, genre);
            ps.setString(3, language);
            ps.setInt(4, duration);
            ps.setInt(5, releaseYear);
            ps.setString(6, rating);
            ps.setDouble(7, price);
            ps.setString(8, imageUrl);
            ps.setString(9, description);
            ps.setInt(10, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public int GetAmountOfMovies() {

        String sql = "SELECT COUNT(*) FROM movies";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
}
