package com.quizapp.dao;

import com.quizapp.db.DBConnection;
import com.quizapp.model.QuizResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    public void save(int userId, int categoryId, int score, int total) throws SQLException {
        String sql = "INSERT INTO results (user_id, category_id, score, total) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, categoryId);
            ps.setInt(3, score);
            ps.setInt(4, total);
            ps.executeUpdate();
        }
    }

    /** Most recent attempts of one user. */
    public List<QuizResult> findByUser(int userId, int limit) throws SQLException {
        String sql = "SELECT u.username, c.name, r.score, r.total, r.taken_at "
                   + "FROM results r JOIN users u ON u.id = r.user_id "
                   + "JOIN categories c ON c.id = r.category_id "
                   + "WHERE r.user_id = ? ORDER BY r.taken_at DESC, r.id DESC LIMIT ?";
        return query(sql, userId, limit);
    }

    /** Best attempts of everyone (highest percentage first, earlier attempt wins ties). */
    public List<QuizResult> leaderboard(int limit) throws SQLException {
        String sql = "SELECT u.username, c.name, r.score, r.total, r.taken_at "
                   + "FROM results r JOIN users u ON u.id = r.user_id "
                   + "JOIN categories c ON c.id = r.category_id "
                   + "ORDER BY (r.score / r.total) DESC, r.taken_at ASC LIMIT ?";
        return query(sql, null, limit);
    }

    // Shared helper: userId == null means "no user filter".
    private List<QuizResult> query(String sql, Integer userId, int limit) throws SQLException {
        List<QuizResult> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int idx = 1;
            if (userId != null) ps.setInt(idx++, userId);
            ps.setInt(idx, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new QuizResult(
                            rs.getString("username"),
                            rs.getString("name"),
                            rs.getInt("score"),
                            rs.getInt("total"),
                            rs.getTimestamp("taken_at").toLocalDateTime()));
                }
            }
        }
        return list;
    }
}
