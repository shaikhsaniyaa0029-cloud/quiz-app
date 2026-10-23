package com.quizapp;

import com.quizapp.dao.UserDAO;
import com.quizapp.model.User;
import com.quizapp.service.QuizService;
import com.quizapp.util.InputHelper;

import java.sql.SQLException;
import java.util.Optional;

/** Entry point: login screen -> player menu. */
public class Main {

    private static final UserDAO userDAO = new UserDAO();
    private static final QuizService quizService = new QuizService();

    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("   CONSOLE QUIZ APPLICATION");
        System.out.println("=================================");

        boolean running = true;
        while (running) {
            System.out.println("\n1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            int choice = InputHelper.readInt("Choose: ", 1, 3);

            try {
                switch (choice) {
                    case 1 -> login();
                    case 2 -> register();
                    case 3 -> running = false;
                }
            } catch (SQLException e) {
                // Any database problem ends up here instead of crashing the app.
                System.out.println("\nDatabase error: " + e.getMessage());
                System.out.println("Check db.properties and that MySQL is running.");
            }
        }
        System.out.println("Goodbye!");
    }

    private static void register() throws SQLException {
        System.out.println("\n--- Register ---");
        String username = InputHelper.readNonEmpty("Username: ");
        if (username.length() > 50) {
            System.out.println("Username must be 50 characters or fewer.");
            return;
        }
        String password = InputHelper.readNonEmpty("Password (min 4 chars): ");
        if (password.length() < 4) {
            System.out.println("Password too short.");
            return;
        }
        if (userDAO.register(username, password)) {
            System.out.println("Account created! You can log in now.");
        } else {
            System.out.println("That username is already taken.");
        }
    }

    private static void login() throws SQLException {
        System.out.println("\n--- Login ---");
        String username = InputHelper.readNonEmpty("Username: ");
        String password = InputHelper.readNonEmpty("Password: ");

        Optional<User> user = userDAO.login(username, password);
        if (user.isPresent()) {
            System.out.println("\nWelcome, " + user.get().getUsername() + "!");
            userMenu(user.get());
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private static void userMenu(User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Take a quiz");
            System.out.println("2. My history");
            System.out.println("3. Leaderboard");
            if (user.isAdmin()) System.out.println("4. [Admin] Add a question");
            System.out.println("0. Logout");

            int choice = InputHelper.readInt("Choose: ", 0, user.isAdmin() ? 4 : 3);
            try {
                switch (choice) {
                    case 1 -> quizService.takeQuiz(user);
                    case 2 -> quizService.showHistory(user);
                    case 3 -> quizService.showLeaderboard();
                    case 4 -> quizService.addQuestion();
                    case 0 -> loggedIn = false;
                }
            } catch (SQLException e) {
                System.out.println("\nDatabase error: " + e.getMessage());
            }
        }
    }
}
