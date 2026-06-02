package com.example.movie_ticket.database;

import com.example.movie_ticket.model.Session;
import com.example.movie_ticket.model.SessionRow;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO {

    public void addSession(int movieId, String showTime, String hallName, String cinema_name) {
        String sql = "INSERT INTO shows (movie_id, show_time, hall_name, cinema_name) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, movieId);
            ps.setString(2, showTime.trim());
            ps.setString(3, hallName);
            ps.setString(4, cinema_name);

            ps.executeUpdate();
            System.out.println("DONE: Session added!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT s.show_id, s.movie_id, m.title, s.show_time, s.hall_name " +
                "FROM shows s JOIN movies m ON s.movie_id = m.movie_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                sessions.add(new Session(
                        rs.getInt("show_id"),
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getString("hall_name"),
                        rs.getString("cinema_name"),
                        rs.getTimestamp("show_time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public void deleteSession(int id) {
        String sql = "DELETE FROM shows WHERE show_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<SessionRow> GetAllSessions(){
        List<SessionRow> sessions = new ArrayList<>();

        String sql = "SELECT show_id, movie_id, hall_name, cinema_name, show_time FROM shows";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sessions.add(new SessionRow(
                        String.valueOf(rs.getInt("show_id")),
                        String.valueOf(rs.getInt("movie_id")),
                        rs.getString("cinema_name"),
                        rs.getString("hall_name"),
                        rs.getString("show_time")
                        ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return sessions;
    }
    public static int getLastShowId() {
        String sql = "SELECT show_id FROM shows ORDER BY show_id DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("show_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int GetAmountOfSessions() {
        String sql = "select count(*) from shows";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

