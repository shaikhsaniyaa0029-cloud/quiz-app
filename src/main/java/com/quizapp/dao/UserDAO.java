package com.quizapp.dao;

import com.quizapp.db.DBConnection;
import com.quizapp.model.User;
import com.quizapp.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/** All database work related to users. (DAO = Data Access Object) */
public class UserDAO {

    private static final int MYSQL_DUPLICATE_ENTRY = 1062;

    /** @return true if created, false if the username is already taken. */
    public boolean register(String username, String password) throws SQLException {
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hash(password, salt);

        String sql = "INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, salt);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == MYSQL_DUPLICATE_ENTRY) return false;
            throw e;
        }
    }

    /** @return the User if credentials are correct, otherwise empty. */
    public Optional<User> login(String username, String password) throws SQLException {
        String sql = "SELECT id, username, password_hash, salt, role FROM users WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String expected = rs.getString("password_hash");
                    String actual = PasswordUtil.hash(password, rs.getString("salt"));
                    if (expected.equals(actual)) {
                        return Optional.of(new User(rs.getInt("id"), rs.getString("username"), rs.getString("role")));
                    }
                }
            }
        }
        return Optional.empty();
    }
}
