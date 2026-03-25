package com.electrostore.config;

public final class DatabaseConfig {

    public static final String DEFAULT_HOST = "localhost";
    public static final String DEFAULT_PORT = "3306";
    public static final String DEFAULT_DB_NAME = "electro_store";
    public static final String DEFAULT_USERNAME = "root";
    public static final String DEFAULT_PASSWORD = "";

    private DatabaseConfig() {
    }

    private static String host = DEFAULT_HOST;
    private static String port = DEFAULT_PORT;
    private static String dbName = DEFAULT_DB_NAME;
    private static String username = DEFAULT_USERNAME;
    private static String password = DEFAULT_PASSWORD;

    public static synchronized String getHost() {
        return host;
    }

    public static synchronized String getPort() {
        return port;
    }

    public static synchronized String getDbName() {
        return dbName;
    }

    public static synchronized String getUsername() {
        return username;
    }

    public static synchronized String getPassword() {
        return password;
    }

    public static synchronized void update(String hostValue, String portValue, String dbNameValue, String usernameValue, String passwordValue) {
        host = hostValue;
        port = portValue;
        dbName = dbNameValue;
        username = usernameValue;
        password = passwordValue;
    }

    public static String buildRootUrl(String hostValue, String portValue) {
        return "jdbc:mysql://" + hostValue + ":" + portValue + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    public static String buildDbUrl(String hostValue, String portValue, String dbNameValue) {
        return "jdbc:mysql://" + hostValue + ":" + portValue + "/" + dbNameValue + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    public static synchronized String getRootUrl() {
        return buildRootUrl(host, port);
    }

    public static synchronized String getUrl() {
        return buildDbUrl(host, port, dbName);
    }
}
