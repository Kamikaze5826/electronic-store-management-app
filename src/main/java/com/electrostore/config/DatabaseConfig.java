package com.electrostore.config;

public final class DatabaseConfig {
    private DatabaseConfig() {
    }

    public static final String DB_NAME = "electro_store";
    public static final String ROOT_URL = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    public static final String URL = "jdbc:mysql://localhost:3306/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "";
}