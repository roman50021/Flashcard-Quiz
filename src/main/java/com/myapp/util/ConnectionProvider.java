package com.myapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider implements AutoCloseable {
    private final String url;

    public ConnectionProvider(String url) {
        this.url = url;
    }

    public Connection get() throws SQLException {
        return DriverManager.getConnection(url);
    }

    @Override public void close() { /* nothing to close for DriverManager */ }
}
