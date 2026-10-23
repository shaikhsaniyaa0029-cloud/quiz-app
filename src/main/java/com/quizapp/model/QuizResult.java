package com.quizapp.model;

import java.time.LocalDateTime;

/** One finished quiz attempt (used for history and the leaderboard). */
public class QuizResult {
    private final String username;
    private final String categoryName;
    private final int score;
    private final int total;
    private final LocalDateTime takenAt;

    public QuizResult(String username, String categoryName, int score, int total, LocalDateTime takenAt) {
        this.username = username;
        this.categoryName = categoryName;
        this.score = score;
        this.total = total;
        this.takenAt = takenAt;
    }

    public String getUsername() { return username; }
    public String getCategoryName() { return categoryName; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public LocalDateTime getTakenAt() { return takenAt; }

    public double getPercentage() {
        return total == 0 ? 0 : (score * 100.0) / total;
    }
}
