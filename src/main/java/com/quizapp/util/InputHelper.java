package com.quizapp.util;

import java.util.Scanner;

/** Small helpers so the rest of the code never has to worry about bad keyboard input. */
public final class InputHelper {

    private static final Scanner SCANNER = new Scanner(System.in);

    private InputHelper() { }

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    /** Keeps asking until the user types a non-empty line. */
    public static String readNonEmpty(String prompt) {
        String s;
        do {
            s = readLine(prompt);
            if (s.isEmpty()) System.out.println("  Input cannot be empty.");
        } while (s.isEmpty());
        return s;
    }

    /** Keeps asking until the user types a whole number between min and max (inclusive). */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            String s = readLine(prompt);
            try {
                int n = Integer.parseInt(s);
                if (n >= min && n <= max) return n;
            } catch (NumberFormatException ignored) {
                // fall through to the error message
            }
            System.out.printf("  Please enter a number between %d and %d.%n", min, max);
        }
    }

    /** Keeps asking until the user types A, B, C or D (any case). */
    public static char readOption(String prompt) {
        while (true) {
            String s = readLine(prompt).toUpperCase();
            if (s.length() == 1 && "ABCD".contains(s)) return s.charAt(0);
            System.out.println("  Please enter A, B, C or D.");
        }
    }
}
