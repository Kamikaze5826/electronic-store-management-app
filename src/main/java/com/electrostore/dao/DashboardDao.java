package com.electrostore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

import com.electrostore.config.DbConnection;

public class DashboardDao {
    public int countProducts() {
        return countBySql("SELECT COUNT(*) FROM products");
    }

    public int countCustomers() {
        return countBySql("SELECT COUNT(*) FROM customers");
    }

    public int countOrders() {
        return countBySql("SELECT COUNT(*) FROM orders");
    }

    public String topCustomerByTotalSpent() {
        String sql = "SELECT c.full_name, COALESCE(SUM(oi.quantity), 0) AS total_qty, COALESCE(SUM(oi.line_total), 0) AS total_spent " +
            "FROM customers c " +
            "JOIN orders o ON o.customer_id = c.id " +
            "JOIN order_items oi ON oi.order_id = o.id " +
            "GROUP BY c.id, c.full_name " +
            "ORDER BY total_spent DESC, total_qty DESC, c.full_name ASC LIMIT 1";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                NumberFormat vnCurrency = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                String fullName = rs.getString("full_name");
                double totalSpent = rs.getDouble("total_spent");
                return fullName + " (" + vnCurrency.format(totalSpent) + ")";
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot load top customer: " + e.getMessage(), e);
        }

        return "Chua co du lieu";
    }

    public String topProductBySoldQuantity() {
        String sql = "SELECT p.name, COALESCE(SUM(oi.quantity), 0) AS total_qty " +
            "FROM products p " +
            "JOIN order_items oi ON oi.product_id = p.id " +
            "GROUP BY p.id, p.name " +
            "ORDER BY total_qty DESC, p.name ASC LIMIT 1";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String productName = rs.getString("name");
                int quantity = rs.getInt("total_qty");
                return productName + " (" + quantity + " da ban)";
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot load top product: " + e.getMessage(), e);
        }

        return "Chua co du lieu";
    }

    public double totalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount),0) FROM orders";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot load total revenue: " + e.getMessage(), e);
        }

        return 0;
    }

    private int countBySql(String sql) {
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot load dashboard data: " + e.getMessage(), e);
        }

        return 0;
    }
}