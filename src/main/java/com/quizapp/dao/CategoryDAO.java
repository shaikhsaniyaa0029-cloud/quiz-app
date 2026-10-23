package com.quizapp.dao;

import com.quizapp.db.DBConnection;
import com.quizapp.model.Category;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> findAll() throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT id, name FROM categories ORDER BY name";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        }
        return list;
    }
}
