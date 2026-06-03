package com.example.movie_ticket.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SeatDAO {

    public void bookSeat(int seatId) {
        String sql = "UPDATE seats SET is_booked = TRUE WHERE seat_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, seatId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Seat booked successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void freeSeat(int seatId) {
        String sql = "UPDATE seats SET is_booked = FALSE WHERE seat_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, seatId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}