package org.kivislime.currencyexchange;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {
    private static final String URL = System.getenv("DB_URL") != null
            ? System.getenv("DB_URL")
            : "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = System.getenv("DB_USER") != null
            ? System.getenv("DB_USER")
            : "myuser";
    private static final String PASS = System.getenv("DB_PASS") != null
            ? System.getenv("DB_PASS")
            : "mysecretpassword";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't load the driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
