package com.electrostore.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DbInitializer {

    private DbInitializer() {
    }

    public static void initialize() {
        initialize(
                DatabaseConfig.getHost(),
                DatabaseConfig.getPort(),
                DatabaseConfig.getDbName(),
                DatabaseConfig.getUsername(),
                DatabaseConfig.getPassword()
        );
    }

    public static void initialize(String host, String port, String dbName, String username, String password) {
        try (Connection connection = DbConnection.getServerConnection(host, port, username, password); Statement statement = connection.createStatement()) {

            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName
                    + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            statement.executeUpdate("USE " + dbName);

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS products ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "name VARCHAR(150) NOT NULL,"
                    + "brand VARCHAR(100) NOT NULL,"
                    + "category VARCHAR(100) NOT NULL,"
                    + "price DECIMAL(12,2) NOT NULL,"
                    + "stock INT NOT NULL DEFAULT 0,"
                    + "is_active TINYINT(1) NOT NULL DEFAULT 1,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")");

            statement.executeUpdate("ALTER TABLE products ADD COLUMN IF NOT EXISTS is_active TINYINT(1) NOT NULL DEFAULT 1");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS customers ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "full_name VARCHAR(120) NOT NULL,"
                    + "phone VARCHAR(20) NOT NULL,"
                    + "email VARCHAR(120),"
                    + "address VARCHAR(255),"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS orders ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "customer_id INT NOT NULL,"
                    + "total_amount DECIMAL(12,2) NOT NULL,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "CONSTRAINT fk_orders_customers FOREIGN KEY (customer_id) REFERENCES customers(id)"
                    + " ON UPDATE CASCADE ON DELETE RESTRICT"
                    + ")");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS order_items ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "order_id INT NOT NULL,"
                    + "product_id INT NOT NULL,"
                    + "quantity INT NOT NULL,"
                    + "unit_price DECIMAL(12,2) NOT NULL,"
                    + "line_total DECIMAL(12,2) NOT NULL,"
                    + "CONSTRAINT fk_items_orders FOREIGN KEY (order_id) REFERENCES orders(id)"
                    + " ON UPDATE CASCADE ON DELETE CASCADE,"
                    + "CONSTRAINT fk_items_products FOREIGN KEY (product_id) REFERENCES products(id)"
                    + " ON UPDATE CASCADE ON DELETE RESTRICT"
                    + ")");

        } catch (SQLException e) {
            throw new RuntimeException("Khong the khoi tao CSDL: " + e.getMessage(), e);
        }
    }

        public static void resetDatabase(String host, String port, String dbName, String username, String password) {
                String safeDbName = validateDbName(dbName);
                try (Connection connection = DbConnection.getServerConnection(host, port, username, password); Statement statement = connection.createStatement()) {
                        statement.executeUpdate("DROP DATABASE IF EXISTS `" + safeDbName + "`");
                } catch (SQLException e) {
                        throw new RuntimeException("Khong the xoa CSDL: " + e.getMessage(), e);
                }

                initialize(host, port, dbName, username, password);
        }

        private static String validateDbName(String dbName) {
                if (dbName == null || dbName.isBlank()) {
                        throw new RuntimeException("Ten CSDL khong hop le");
                }
                if (!dbName.matches("[A-Za-z0-9_]+")) {
                        throw new RuntimeException("Ten CSDL chi duoc chua chu, so va dau gach duoi");
                }
                return dbName;
        }
}
