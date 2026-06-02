package com.example.movie_ticket.database;

import com.example.movie_ticket.model.CustomerRow;
import com.example.movie_ticket.model.Notification;
import com.example.movie_ticket.model.Product;
import com.example.movie_ticket.service.UserSession;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public boolean registerCustomer(String name, String surname, String login, String password, String email, String phone) {
        String insertAccount = "INSERT INTO accounts (login, password, email) VALUES (?, ?, ?)";
        String insertPerson = "INSERT INTO users (name, surname, login_fk, phone) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psAcc = conn.prepareStatement(insertAccount)) {
                psAcc.setString(1, login);
                psAcc.setString(2, password);
                psAcc.setString(3, email);
                psAcc.executeUpdate();
            }

            try (PreparedStatement psPers = conn.prepareStatement(insertPerson)) {
                psPers.setString(1, name);
                psPers.setString(2, surname);
                psPers.setString(3, login);
                psPers.setString(4, phone);
                psPers.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                Connection conn = DatabaseConnection.getConnection();
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

//    public String authenticateUser(String login, String password) {
//        String sql = "SELECT role FROM accounts WHERE login = ? AND password = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, login);
//            ps.setString(2, password);
//
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getString("role");
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public void loadUserDataToSession(String login) {
        String query =
                "SELECT a.login, a.email, a.role, " +
                        "       u.id, u.name, u.surname, u.phone, " +
                        "       u.balance, u.status, u.booked_tickets " +
                        "FROM accounts a " +
                        "JOIN users u ON a.login = u.login_fk " +
                        "WHERE a.login = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserSession.getInstance().loginUser(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("role"),
                            rs.getString("phone"),
                            rs.getBigDecimal("balance"),
                            rs.getString("status"),
                            rs.getInt("booked_tickets"),
                            rs.getString("login"),
                            rs.getString("email")
                    );
                    System.out.println("✅ Session loaded for: " +
                            rs.getString("name"));
                } else {
                    System.out.println("⚠️ No users row linked to: " + login);
                }
            }
        } catch (Exception e) {
            System.err.println("loadUserDataToSession error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getNumberOfCustomers() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public List<CustomerRow> getAllCustomers() {
        List<CustomerRow> customersList = new ArrayList<>();
        String sql = "SELECT id, registered_date, booked_tickets, status FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String regDate = rs.getString("registered_date");
                String bookedTickets = String.valueOf(rs.getInt("booked_tickets"));
                String status = rs.getString("status");

                if (status == null) {
                    status = "Active";
                }

                CustomerRow row = new CustomerRow(id, regDate, bookedTickets, status);
                customersList.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customersList;
    }
//    public List<CustomerRow> getAllAccounts() {
//        List<CustomerRow> accountsList = new ArrayList<>();
//        String sql = "SELECT id, registered_date, booked_tickets, status FROM users";
//        String sql1 = "SELECT login, email FROM accounts";
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql);
//             PreparedStatement ps1 = conn.prepareStatement(sql1);
//             ResultSet rs = ps.executeQuery();
//             ResultSet rs1 = ps1.executeQuery()) {
//
//            while (rs.next() && rs1.next()) {
//                String id = rs.getString("id");
//                String regDate = rs.getString("registered_date");
//                String login =  rs1.getString("login");
//                String email = rs1.getString("email");
//                String bookedTickets = String.valueOf(rs.getInt("booked_tickets"));
//                String status = rs.getString("status");
//
//                if (status == null) {
//                    status = "Active";
//                }
//
//                CustomerRow row = new CustomerRow(id, regDate, email, login, bookedTickets, status);
//                accountsList.add(row);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return accountsList;
//    }
    public List<CustomerRow> getAllAccounts() {
        List<CustomerRow> accountsList = new ArrayList<>();

        String sql =
                "SELECT u.id, u.registered_date, u.booked_tickets, u.status, a.login, a.email " +
                        "FROM users u " +
                        "JOIN accounts a ON u.login_fk = a.login";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String regDate = rs.getString("registered_date");
                String login = rs.getString("login");
                String email = rs.getString("email");
                String bookedTickets = String.valueOf(rs.getInt("booked_tickets"));
                String status = rs.getString("status");

                if (status == null) {
                    status = "Active";
                }

                accountsList.add(new CustomerRow(id, regDate, email, login, bookedTickets, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accountsList;
    }

    public String getStatusFromDB(String login, String password) {
        String sql =
                "SELECT u.status " +
                        "FROM accounts a " +
                        "JOIN users u ON a.login = u.login_fk " +
                        "WHERE a.login = ? AND a.password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    return status == null ? "Active" : status;
                }
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.getStatusFromDB: " + e.getMessage());
        }
        return null;
    }

    public boolean updateUserStatus(String userId, String newStatus) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setString(2, userId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public String authenticateUser(String login, String password) {
        String sql =
                    "SELECT a.role " +
                            "FROM accounts a " +
                            "JOIN users u ON a.login = u.login_fk " +
                            "WHERE a.login = ? " +
                            "  AND a.password = ? " +
                            "  AND (u.status IS NULL OR u.status <> 'Blocked')";

        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
                ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("role");
            }
        }
    } catch (SQLException e) {
            e.printStackTrace();
        }
                return null;
    }


    public double getBalance(String userId) {
        String sql = "SELECT balance FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("balance");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    public void updateBalance(String userId, double newBalance) {
        String sql = "UPDATE users SET balance = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newBalance);
            ps.setString(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static UserProfileData loadProfile() {
        int userId = UserSession.getInstance().getUserId();
        if (userId <= 0) return new UserProfileData("", "", "", "");

        String sql =
                "SELECT u.name, u.surname, u.phone, a.email " +
                        "FROM users u " +
                        "JOIN accounts a ON u.login_fk = a.login " +
                        "WHERE u.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserProfileData(
                            nvl(rs.getString("name")),
                            nvl(rs.getString("surname")),
                            nvl(rs.getString("phone")),
                            nvl(rs.getString("email"))
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.loadProfile: " + e.getMessage());
        }
        return new UserProfileData("", "", "", "");
    }

    public static boolean saveProfile(String firstName, String lastName, String email, String phone) {
        int userId = UserSession.getInstance().getUserId();
        String sql = "UPDATE users SET name=?, surname=?, phone=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, phone);
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("UserDAO.saveProfile: " + e.getMessage());
            return false;
        }
    }

    public static List<TicketData> loadTickets() {
        List<TicketData> list = new ArrayList<>();
        int userId = UserSession.getInstance().getUserId();
        if (userId <= 0) return list;

        String sql =
                "SELECT t.ticket_id, t.booking_id, t.movie_title, " +
                        "       t.price, t.show_time, t.booked_at, b.payment_method " +
                        "FROM booked_tickets t " +
                        "JOIN bookings b ON t.booking_id = b.booking_id " +
                        "WHERE b.user_id = ? " +
                        "ORDER BY t.booked_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String raw = rs.getString("show_time");

                    System.out.println("Raw show_time from DB: '" + raw + "'");

                    LocalDateTime showTime = parseShowTime(raw);

                    System.out.println("Parsed showTime: " + showTime);

                    Timestamp bookedTs = rs.getTimestamp("booked_at");
                    String bookedAt = bookedTs != null
                            ? bookedTs.toLocalDateTime()
                            .format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))
                            : "N/A";

                    list.add(new TicketData(
                            rs.getInt("ticket_id"),
                            rs.getString("booking_id"),
                            rs.getString("movie_title"),
                            rs.getDouble("price"),
                            showTime,
                            bookedAt,
                            nvl(rs.getString("payment_method"))
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.loadTickets: " + e.getMessage());
        }
        return list;
    }

    public static LocalDateTime parseShowTime(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            System.err.println("parseShowTime: raw is null or empty");
            return null;
        }
        String s = raw.trim();
        try {
            if (s.length() >= 16) {
                return LocalDateTime.parse(
                        s.substring(0, 16),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                );
            }
        } catch (Exception e) {
            System.err.println("parseShowTime failed for: '" + s + "' — " + e.getMessage());
        }
        return null;
    }

    public static boolean deleteTicket(int ticketId) {
        String sql = "DELETE FROM booked_tickets WHERE ticket_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("UserDAO.deleteTicket: " + e.getMessage());
            return false;
        }
    }

    public static List<Notification> loadNotifications() {
        return Notification.loadForCurrentUser();
    }

    public static void sendNotification(int userId, String title, String desc) {
        Notification.send(userId, title, desc);
    }

    public static void markNotificationRead(int notifId) {
        Notification.markRead(notifId);
    }

    public static void markAllNotificationsRead(int userId) {
        Notification.markAllRead(userId);
    }

    public static String loadShowTimesForMovie(String movieTitle) {
        String sql =
                "SELECT DISTINCT s.show_time " +
                        "FROM shows s " +
                        "JOIN movies m ON s.movie_id = m.movie_id " +
                        "WHERE m.title = ? " +
                        "ORDER BY s.show_time " +
                        "LIMIT 4";

        StringBuilder result = new StringBuilder();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, movieTitle);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String raw = rs.getString("show_time");
                    if (raw != null && raw.length() >= 16) {
                        if (result.length() > 0) result.append("  •  ");
                        result.append(raw.substring(11, 16));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("UserDAO.loadShowTimes: " + e.getMessage());
        }
        return result.length() > 0 ? result.toString() : "No showtimes";
    }

    public static class UserProfileData {
        public final String name, surname, phone, email;

        public UserProfileData(String name, String surname,
                               String phone, String email) {
            this.name    = name    != null ? name    : "";
            this.surname = surname != null ? surname : "";
            this.phone   = phone   != null ? phone   : "";
            this.email   = email   != null ? email   : "";
        }
    }

    public static class TicketData {
        public final int           ticketId;
        public final String        bookingId;
        public final String        movieTitle;
        public final double        price;
        public final LocalDateTime showTime;
        public final String        bookedAt;
        public final String        payMethod;

        public TicketData(int ticketId, String bookingId, String movieTitle,
                          double price, LocalDateTime showTime,
                          String bookedAt, String payMethod) {
            this.ticketId   = ticketId;
            this.bookingId  = bookingId;
            this.movieTitle = movieTitle;
            this.price      = price;
            this.showTime   = showTime;
            this.bookedAt   = bookedAt;
            this.payMethod  = payMethod;
        }

        public boolean isRefundEligible() {
            if (showTime == null) return false;
            return LocalDateTime.now().plusHours(2).isBefore(showTime);
        }
    }

    public void savePurchase(String userId, int productId) {
        String sql = "INSERT INTO user_products (user_id, product_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deletePurchase(String userId, int productId) {
        String sql = "DELETE FROM user_products WHERE user_id = ? AND product_id = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Product> getUserProducts(String userId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.* FROM products p " +
                "JOIN user_products up ON p.id = up.product_id " +
                "WHERE up.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getString("image_path"),
                            rs.getInt("quantity")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return products;
    }

    private static String nvl(String s) { return s != null ? s : ""; }
    public static boolean saveEmail(String newEmail) {
        String loginFk = UserSession.getInstance().getLoginFl();
        String sql = "UPDATE accounts SET email = ? WHERE login = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newEmail);
            ps.setString(2, loginFk);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("UserDAO.saveEmail: " + e.getMessage());
            return false;
        }
    }
    public static void awardCoins(int userId, double ticketTotal) {
        double coinsEarned = ticketTotal * 0.10;
        String sql = "UPDATE users SET balance = balance + ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, coinsEarned);
            ps.setInt(2, userId);
            ps.executeUpdate();
            System.out.println("✅ Awarded " + coinsEarned + " coins to user " + userId);
        } catch (SQLException e) {
            System.err.println("UserDAO.awardCoins: " + e.getMessage());
        }
    }
//    public String getStatusFromDB(String login, String password) {
//        String sql = "SELECT status FROM accounts WHERE login = ? AND password = ?";
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, login);
//            ps.setString(2, password);
//
//            ResultSet rs = ps.executeQuery();
//
//            if (rs.next()) {
//                return rs.getString("status");
//            }
//
//        } catch (SQLException e) {
//            System.err.println("UserDAO.getStatusFromDB: " + e.getMessage());
//        }
//
//        return null;
//    }
}

