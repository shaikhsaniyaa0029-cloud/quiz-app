package com.quizapp.service;

import com.quizapp.dao.CategoryDAO;
import com.quizapp.dao.QuestionDAO;
import com.quizapp.dao.ResultDAO;
import com.quizapp.model.Category;
import com.quizapp.model.Question;
import com.quizapp.model.QuizResult;
import com.quizapp.model.User;
import com.quizapp.util.InputHelper;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** The "brain" of the app: quiz flow, scoring, history, leaderboard, admin tools. */
public class QuizService {

    private static final int QUESTIONS_PER_QUIZ = 5;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final ResultDAO resultDAO = new ResultDAO();

    // ---------- Take a quiz ----------
    public void takeQuiz(User user) throws SQLException {
        Category category = chooseCategory();
        if (category == null) return;

        List<Question> questions = questionDAO.findRandomByCategory(category.getId(), QUESTIONS_PER_QUIZ);
        if (questions.isEmpty()) {
            System.out.println("No questions in this category yet.");
            return;
        }

        System.out.println("\n=== " + category.getName() + " Quiz (" + questions.size() + " questions) ===");
        int score = 0;
        int number = 1;

        for (Question q : questions) {
            System.out.printf("%nQ%d. %s%n", number++, q.getText());
            String[] opts = q.getOptions();
            for (int i = 0; i < 4; i++) {
                System.out.printf("   %c) %s%n", (char) ('A' + i), opts[i]);
            }

            char answer = InputHelper.readOption("Your answer (A-D): ");
            if (q.isCorrect(answer)) {
                score++;
                System.out.println("  Correct!");
            } else {
                System.out.println("  Wrong. Correct answer: " + q.getCorrectOption() + ") " + q.getCorrectText());
            }
        }

        resultDAO.save(user.getId(), category.getId(), score, questions.size());
        printScoreCard(user, category, score, questions.size());
    }

    private void printScoreCard(User user, Category category, int score, int total) {
        double pct = (score * 100.0) / total;
        String remark = pct == 100 ? "Perfect!" : pct >= 60 ? "Good job!" : "Keep practising!";
        System.out.println("\n----------------------------------");
        System.out.println(" Player   : " + user.getUsername());
        System.out.println(" Category : " + category.getName());
        System.out.printf(" Score    : %d / %d (%.0f%%)%n", score, total, pct);
        System.out.println(" Result   : " + remark);
        System.out.println("----------------------------------");
    }

    // ---------- History & leaderboard ----------
    public void showHistory(User user) throws SQLException {
        List<QuizResult> list = resultDAO.findByUser(user.getId(), 10);
        System.out.println("\n=== Your last attempts ===");
        if (list.isEmpty()) {
            System.out.println("You haven't taken any quiz yet.");
            return;
        }
        System.out.printf("%-18s %-17s %-8s %s%n", "Date", "Category", "Score", "Percent");
        for (QuizResult r : list) {
            System.out.printf("%-18s %-17s %-8s %.0f%%%n",
                    r.getTakenAt().format(FMT), r.getCategoryName(),
                    r.getScore() + "/" + r.getTotal(), r.getPercentage());
        }
    }

    public void showLeaderboard() throws SQLException {
        List<QuizResult> list = resultDAO.leaderboard(10);
        System.out.println("\n=== Leaderboard (Top 10) ===");
        if (list.isEmpty()) {
            System.out.println("No results yet. Be the first!");
            return;
        }
        System.out.printf("%-4s %-15s %-17s %-8s %s%n", "#", "Player", "Category", "Score", "Percent");
        int rank = 1;
        for (QuizResult r : list) {
            System.out.printf("%-4d %-15s %-17s %-8s %.0f%%%n",
                    rank++, r.getUsername(), r.getCategoryName(),
                    r.getScore() + "/" + r.getTotal(), r.getPercentage());
        }
    }

    // ---------- Admin ----------
    public void addQuestion() throws SQLException {
        Category category = chooseCategory();
        if (category == null) return;

        System.out.println("\n=== Add a new question to " + category.getName() + " ===");
        String text = InputHelper.readNonEmpty("Question text : ");
        String[] options = new String[4];
        for (int i = 0; i < 4; i++) {
            options[i] = InputHelper.readNonEmpty("Option " + (char) ('A' + i) + "      : ");
        }
        char correct = InputHelper.readOption("Correct option (A-D): ");

        questionDAO.add(new Question(0, category.getId(), text, options, correct));
        System.out.println("Question added successfully.");
    }

    // ---------- Helper ----------
    private Category chooseCategory() throws SQLException {
        List<Category> categories = categoryDAO.findAll();
        if (categories.isEmpty()) {
            System.out.println("No categories found. Did you run sql/schema.sql?");
            return null;
        }
        System.out.println("\nChoose a category:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, categories.get(i).getName());
        }
        System.out.println("  0. Back");
        int choice = InputHelper.readInt("Your choice: ", 0, categories.size());
        return choice == 0 ? null : categories.get(choice - 1);
    }
}
