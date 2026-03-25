package com.electrostore.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.electrostore.config.DbConnection;
import com.electrostore.model.Customer;
import com.electrostore.model.OrderItem;
import com.electrostore.model.OrderSummary;
import com.electrostore.model.Product;

public class OrderService {
    public List<Product> getInStockProducts() {
        String sql = "SELECT id, name, brand, category, price, stock FROM products WHERE is_active = 1 AND stock > 0 ORDER BY name";
        List<Product> products = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("brand"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot load in-stock products", e);
        }

        return products;
    }

    public List<Customer> getCustomers() {
        String sql = "SELECT id, full_name, phone, email, address FROM customers ORDER BY full_name";
        List<Customer> customers = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                customers.add(new Customer(
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot load customers for order", e);
        }

        return customers;
    }

    public void createOrder(int customerId, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        String insertOrderSql = "INSERT INTO orders(customer_id, total_amount) VALUES(?,?)";
        String insertItemSql = "INSERT INTO order_items(order_id, product_id, quantity, unit_price, line_total) VALUES(?,?,?,?,?)";
        String updateStockSql = "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";

        try (Connection connection = DbConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                double total = items.stream().mapToDouble(OrderItem::getLineTotal).sum();

                int orderId;
                try (PreparedStatement orderPs = connection.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                    orderPs.setInt(1, customerId);
                    orderPs.setDouble(2, total);
                    orderPs.executeUpdate();
                    try (ResultSet keys = orderPs.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Cannot get generated order id");
                        }
                        orderId = keys.getInt(1);
                    }
                }

                try (PreparedStatement itemPs = connection.prepareStatement(insertItemSql);
                     PreparedStatement stockPs = connection.prepareStatement(updateStockSql)) {

                    for (OrderItem item : items) {
                        itemPs.setInt(1, orderId);
                        itemPs.setInt(2, item.getProductId());
                        itemPs.setInt(3, item.getQuantity());
                        itemPs.setDouble(4, item.getUnitPrice());
                        itemPs.setDouble(5, item.getLineTotal());
                        itemPs.addBatch();

                        stockPs.setInt(1, item.getQuantity());
                        stockPs.setInt(2, item.getProductId());
                        stockPs.setInt(3, item.getQuantity());
                        stockPs.addBatch();
                    }

                    itemPs.executeBatch();
                    int[] updatedRows = stockPs.executeBatch();
                    for (int rows : updatedRows) {
                        if (rows == 0) {
                            throw new SQLException("Not enough stock for one or more products");
                        }
                    }
                }

                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw new RuntimeException("Cannot create order", e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot create order", e);
        }
    }

    public List<OrderSummary> getRecentOrders() {
        String sql = "SELECT o.id, c.full_name, o.total_amount, o.created_at " +
            "FROM orders o JOIN customers c ON c.id = o.customer_id ORDER BY o.id DESC LIMIT 15";

        List<OrderSummary> orderSummaries = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Timestamp created = rs.getTimestamp("created_at");
                LocalDateTime createdAt = created == null ? null : created.toLocalDateTime();

                orderSummaries.add(new OrderSummary(
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getDouble("total_amount"),
                    createdAt
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot load recent orders", e);
        }

        return orderSummaries;
    }

    public List<OrderSummary> getOrdersByCustomer(int customerId) {
        String sql = "SELECT o.id, c.full_name, o.total_amount, o.created_at " +
            "FROM orders o JOIN customers c ON c.id = o.customer_id " +
            "WHERE o.customer_id = ? ORDER BY o.id DESC";

        List<OrderSummary> orderSummaries = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp created = rs.getTimestamp("created_at");
                    LocalDateTime createdAt = created == null ? null : created.toLocalDateTime();

                    orderSummaries.add(new OrderSummary(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getDouble("total_amount"),
                        createdAt
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot load customer orders", e);
        }

        return orderSummaries;
    }

    public List<OrderItem> getOrderItemsByOrder(int orderId) {
        String sql = "SELECT oi.product_id, p.name AS product_name, oi.quantity, oi.unit_price " +
            "FROM order_items oi JOIN products p ON p.id = oi.product_id " +
            "WHERE oi.order_id = ? ORDER BY oi.id";

        List<OrderItem> items = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new OrderItem(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot load order items", e);
        }

        return items;
    }
}