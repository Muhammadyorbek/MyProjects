package com.example.movie_ticket.service;

import com.example.movie_ticket.database.DatabaseConnection;
import com.example.movie_ticket.model.CouponRow;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CouponService {

    public static double getDiscountPercentage(String couponCode) {
        String query = "SELECT discount_percentage FROM coupons WHERE coupon_code = ? AND is_active = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, couponCode.toUpperCase().trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("discount_percentage");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public boolean addCoupon(String couponCode, Double discountPercentage, boolean isActive){
        String query = "INSERT INTO coupons (coupon_code, discount_percentage, is_active) VALUES (?, ?, ?)";
        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, couponCode.toUpperCase());
            stmt.setDouble(2, discountPercentage);
            stmt.setInt(3, isActive ? 1 : 0);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<CouponRow> getAllCoupons() {
        List<CouponRow> list = new ArrayList<>();
        String query = "SELECT coupon_code, discount_percentage, is_active FROM coupons";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String code = rs.getString("coupon_code");

                double percentageRaw = rs.getDouble("discount_percentage");
                String percentage = String.valueOf(percentageRaw);

                String status = String.valueOf(rs.getInt("is_active"));

                list.add(new CouponRow(code, percentage, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean isCodeExists(String code) {
        String query = "SELECT COUNT(*) FROM coupons WHERE coupon_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code.toUpperCase().trim());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    public boolean deleteCoupon(String couponCode) {
        String query = "DELETE FROM coupons WHERE coupon_code = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, couponCode.toUpperCase().trim());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete error: " + e.getMessage());
            return false;
        }
    }

    public boolean toggleActive(String couponCode, boolean newStatus) {
        String query = "UPDATE coupons SET is_active = ? WHERE coupon_code = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, newStatus ? 1 : 0);
            stmt.setString(2, couponCode.toUpperCase().trim());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Toggle error: " + e.getMessage());
            return false;
        }
    }
}
