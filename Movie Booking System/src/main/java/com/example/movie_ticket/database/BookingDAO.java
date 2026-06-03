package com.example.movie_ticket.database;

import java.sql.*;

public class BookingDAO {

    public void bookSeat(int userId, int showId, String seatNumber) {
        String sql = """
                INSERT INTO bookings
                (user_id, show_id, seat_number)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, showId);
            stmt.setString(3, seatNumber);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Booking successful!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void bookMultipleSeats(int userId, int showId, String[] seatNumbers) {

        for (String seat : seatNumbers) {
            bookSeat(userId, showId, seat);
        }

        System.out.println("All selected seats booked successfully!");
    }


    public void CancelBooking(int BookingId, String Seat_number) {
        String sql1 = "DELETE FROM bookings WHERE booking_id = ?";
        String sql2 = "UPDATE seats SET is_booked = FALSE WHERE seat_number = ?";

        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement stm = connection.prepareStatement(sql1)){
                stm.setInt(1, BookingId);
                stm.executeUpdate();
            }

            try(PreparedStatement stm1 = connection.prepareStatement(sql2)){
                stm1.setString(1, Seat_number);
                stm1.executeUpdate();
            }

            System.out.println("Booking successfully canceled!");
        }
        catch(SQLException e){
            e.printStackTrace();
        }


    }
}