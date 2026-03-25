package com.electrostore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.electrostore.config.DbConnection;
import com.electrostore.model.Customer;

public class CustomerDao {
    public List<Customer> findAll() {
        String sql = "SELECT id, full_name, phone, email, address FROM customers ORDER BY id DESC";
        List<Customer> customers = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                customers.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot load customers", e);
        }

        return customers;
    }
    
    public List<Customer> searchByKeyword(String keyword) {
        String sql = "SELECT id, full_name, phone, email, address " +
            "FROM customers WHERE full_name LIKE ? OR phone LIKE ? OR email LIKE ? ORDER BY id DESC";
        List<Customer> customers = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot search customers", e);
        }

        return customers;
    }
    public void insert(Customer customer) {
        String sql = "INSERT INTO customers(full_name, phone, email, address) VALUES(?,?,?,?)";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getAddress());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot insert customer", e);
        }
    }

    public void update(Customer customer) {
        String sql = "UPDATE customers SET full_name=?, phone=?, email=?, address=? WHERE id=?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getAddress());
            ps.setInt(5, customer.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot update customer", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM customers WHERE id=?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot delete customer", e);
        }
    }

    public Customer findById(int id) {
        String sql = "SELECT id, full_name, phone, email, address FROM customers WHERE id=?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot find customer", e);
        }

        return null;
    }

    private Customer mapResultSet(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("id"),
            rs.getString("full_name"),
            rs.getString("phone"),
            rs.getString("email"),
            rs.getString("address")
        );
    }
}