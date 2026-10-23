package com.quizapp.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Salted SHA-256 hashing so passwords are never stored in plain text.
 * NOTE: fine for learning. Real production systems should use bcrypt/Argon2.
 */
public final class PasswordUtil {

    private PasswordUtil() { }

    public static String generateSalt() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return toHex(bytes); // 32 hex characters
    }

    public static String hash(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest((salt + password).getBytes(StandardCharsets.UTF_8));
            return toHex(digest); // 64 hex characters
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
