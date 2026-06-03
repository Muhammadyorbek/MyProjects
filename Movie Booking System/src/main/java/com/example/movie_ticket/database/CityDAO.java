package com.example.movie_ticket.database;

import com.example.movie_ticket.model.City;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CityDAO {

    public List<City> getAllCities() {
        List<City> cities = new ArrayList<>();
        String sql = "SELECT * FROM cities";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cities.add(new City(rs.getInt("city_id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cities;
    }

    public City findCityByName(String cityName) {
        String sql = "SELECT * FROM cities WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cityName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new City(rs.getInt("city_id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addCity(String name) {
        String sql = "INSERT INTO cities (name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.executeUpdate();
            System.out.println("Город '" + name + "' успешно добавлен в БД.");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Ошибка: Город с таким названием уже существует.");
            } else {
                e.printStackTrace();
            }
        }
    }

    public void deleteCity(String name) {
        String sql = "DELETE FROM cities WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.executeUpdate();
            System.out.println("Город '" + name + "' удален.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCity(int cityId, String newName) {
        String sql = "UPDATE cities SET name = ? WHERE city_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newName);
            pstmt.setInt(2, cityId);
            pstmt.executeUpdate();
            System.out.println("Город обновлен в базе.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}