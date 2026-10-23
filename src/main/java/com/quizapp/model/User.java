package com.quizapp.model;

/** A logged-in user. We never keep the password in memory after login. */
public class User {
    private final int id;
    private final String username;
    private final String role; // "PLAYER" or "ADMIN"

    public User(int id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public boolean isAdmin() { return "ADMIN".equals(role); }
}
