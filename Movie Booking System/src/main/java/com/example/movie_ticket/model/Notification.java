package com.example.movie_ticket.model;

import com.example.movie_ticket.database.DatabaseConnection;
import com.example.movie_ticket.service.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Notification {
    private final int id;
    private final int userId;
    private final String title;
    private final String description;
    private final boolean isRead;
    private final LocalDateTime createdAt;

    public Notification(int id, int userId, String title,
                        String description, boolean isRead, LocalDateTime createdAt) {
        this.id  = id;
        this.userId = userId;
        this.title= title;
        this.description = description;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public int           getId()          { return id; }
    public int           getUserId()      { return userId; }
    public String        getTitle()       { return title; }
    public String        getDescription() { return description; }
    public boolean       isRead()         { return isRead; }
    public LocalDateTime getCreatedAt()   { return createdAt; }


    public static List<Notification> loadForCurrentUser() {
        List<Notification> list = new ArrayList<>();
        int userId = UserSession.getInstance().getUserId();
        if (userId <= 0) return list;

        String sql = "SELECT notification_id, user_id, title, description, is_read, created_at " +
                "FROM notifications WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Notification(
                            rs.getInt("notification_id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getBoolean("is_read"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("loadForCurrentUser error: " + e.getMessage());
        }
        return list;
    }

    public static void send(int userId, String title, String description) {
        if (userId <= 0) return;
        String sql = "INSERT INTO notifications (user_id, title, description) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void markRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE notification_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, notificationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void markAllRead(int userId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
