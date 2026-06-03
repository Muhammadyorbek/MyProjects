package com.example.movie_ticket.database;

import com.example.movie_ticket.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShopDAO {


    public List<Product> getAllProducts() {

        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM shop_items";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Product product = new Product(
                        rs.getInt("item_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("image_path"),
                        rs.getInt("quantity")
                );

                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }


    public void addProduct(String name,
                           double price,
                           String imagePath,
                           int quantity) {

        String sql = """
                INSERT INTO shop_items
                (name, price, image_path, quantity)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, imagePath);
            ps.setInt(4, quantity);

            ps.executeUpdate();

            System.out.println("Product added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteProductById(int id) {
        String sql = "DELETE FROM shop_items WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Product deleted by ID!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateProduct(int id, String name, double price, String imagePath, int quantity) {
        String sql = "UPDATE shop_items SET name = ?, price = ?, image_path = ?, quantity = ? WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, imagePath);
            ps.setInt(4, quantity);
            ps.setInt(5, id);

            ps.executeUpdate();
            System.out.println("Product updated in DB!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void reduceQuantity(int itemId) {

        String sql = """
                UPDATE shop_items
                SET quantity = quantity - 1
                WHERE item_id = ? AND quantity > 0
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Quantity updated!");
            } else {
                System.out.println("Item out of stock!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getQuantity(int itemId) {

        String sql = "SELECT quantity FROM shop_items WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("quantity");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void increaseQuantity(int itemId) {

        String sql = """
                UPDATE shop_items
                SET quantity = quantity + 1
                WHERE item_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Quantity increased!");
            } else {
                System.out.println("Item not found!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
