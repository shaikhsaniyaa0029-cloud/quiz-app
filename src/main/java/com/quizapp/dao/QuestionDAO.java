package com.quizapp.dao;

import com.quizapp.db.DBConnection;
import com.quizapp.model.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    /** Picks up to 'limit' random questions from a category. */
    public List<Question> findRandomByCategory(int categoryId, int limit) throws SQLException {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE category_id = ? ORDER BY RAND() LIMIT ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] options = {
                            rs.getString("option_a"), rs.getString("option_b"),
                            rs.getString("option_c"), rs.getString("option_d")
                    };
                    list.add(new Question(
                            rs.getInt("id"),
                            rs.getInt("category_id"),
                            rs.getString("question_text"),
                            options,
                            rs.getString("correct_option").charAt(0)));
                }
            }
        }
        return list;
    }

    /** Admin feature: add a new question. */
    public void add(Question q) throws SQLException {
        String sql = "INSERT INTO questions (category_id, question_text, option_a, option_b, option_c, option_d, correct_option) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, q.getCategoryId());
            ps.setString(2, q.getText());
            for (int i = 0; i < 4; i++) {
                ps.setString(3 + i, q.getOptions()[i]);
            }
            ps.setString(7, String.valueOf(q.getCorrectOption()));
            ps.executeUpdate();
        }
    }
}
