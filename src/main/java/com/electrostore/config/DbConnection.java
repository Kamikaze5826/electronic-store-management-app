package com.electrostore.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbConnection {

    private DbConnection() {
    }

    public static Connection getServerConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.getRootUrl(),
                DatabaseConfig.getUsername(),
                DatabaseConfig.getPassword()
        );
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.getUrl(),
                DatabaseConfig.getUsername(),
                DatabaseConfig.getPassword()
        );
    }

    public static Connection getServerConnection(String host, String port, String username, String password) throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.buildRootUrl(host, port),
                username,
                password
        );
    }

    public static Connection getConnection(String host, String port, String dbName, String username, String password) throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.buildDbUrl(host, port, dbName),
                username,
                password
        );
    }
}
