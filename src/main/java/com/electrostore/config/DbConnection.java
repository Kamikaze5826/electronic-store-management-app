package com.electrostore.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbConnection {
    private DbConnection() {
    }

    public static Connection getServerConnection() throws SQLException {
        return DriverManager.getConnection(
            DatabaseConfig.ROOT_URL,
            DatabaseConfig.USERNAME,
            DatabaseConfig.PASSWORD
        );
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            DatabaseConfig.URL,
            DatabaseConfig.USERNAME,
            DatabaseConfig.PASSWORD
        );
    }
}