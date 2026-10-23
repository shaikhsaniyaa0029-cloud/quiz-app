package com.quizapp.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Creates database connections using the settings in db.properties.
 * Each DAO method opens its own short-lived connection (try-with-resources closes it),
 * which keeps the code simple and leak-free for a console app.
 */
public final class DBConnection {

    private static final Properties PROPS = new Properties();

    // Runs once, when the class is first used.
    static {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties not found in src/main/resources");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBConnection() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                PROPS.getProperty("db.url"),
                PROPS.getProperty("db.user"),
                PROPS.getProperty("db.password"));
    }
}
