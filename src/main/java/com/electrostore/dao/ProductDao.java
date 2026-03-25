package com.electrostore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.electrostore.config.DbConnection;
import com.electrostore.model.Product;

public class ProductDao {
    public List<Product> findAll() {
        String sql = "SELECT id, name, brand, category, price, stock FROM products WHERE is_active = 1 ORDER BY id DESC";
        List<Product> products = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot load products: " + formatSqlError(e), e);
        }

        return products;
    }

    public List<Product> searchByKeyword(String keyword) {
        String sql = "SELECT id, name, brand, category, price, stock " +
            "FROM products WHERE is_active = 1 AND (name LIKE ? OR brand LIKE ? OR category LIKE ?) ORDER BY id DESC";
        List<Product> products = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot search products: " + formatSqlError(e), e);
        }

        return products;
    }

    public void insert(Product product) {
        String sql = "INSERT INTO products(name, brand, category, price, stock, is_active) VALUES(?,?,?,?,?,1)";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getBrand());
            ps.setString(3, product.getCategory());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getStock());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot insert product: " + formatSqlError(e), e);
        }
    }

    public void update(Product product) {
        String sql = "UPDATE products SET name=?, brand=?, category=?, price=?, stock=? WHERE id=? AND is_active = 1";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getBrand());
            ps.setString(3, product.getCategory());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getStock());
            ps.setInt(6, product.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot update product: " + formatSqlError(e), e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM products WHERE id=?";
        String softDeleteSql = "UPDATE products SET is_active = 0, stock = 0 WHERE id=?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                try (Connection connection = DbConnection.getConnection();
                     PreparedStatement softDeletePs = connection.prepareStatement(softDeleteSql)) {

                    softDeletePs.setInt(1, id);
                    softDeletePs.executeUpdate();
                    return;
                } catch (SQLException softDeleteEx) {
                    throw new RuntimeException("Cannot deactivate product: " + formatSqlError(softDeleteEx), softDeleteEx);
                }
            }
            throw new RuntimeException("Cannot delete product: " + formatSqlError(e), e);
        }
    }

    public Product findById(int id) {
        String sql = "SELECT id, name, brand, category, price, stock FROM products WHERE id=? AND is_active = 1";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot find product: " + formatSqlError(e), e);
        }

        return null;
    }

    private Product mapResultSet(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("brand"),
            rs.getString("category"),
            rs.getDouble("price"),
            rs.getInt("stock")
        );
    }

    private String formatSqlError(SQLException e) {
        return "[SQLState=" + e.getSQLState() + ", ErrorCode=" + e.getErrorCode() + "] " + e.getMessage();
    }
}