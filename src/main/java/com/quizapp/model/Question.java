package com.quizapp.model;

/** One multiple-choice question with four options (A-D). */
public class Question {
    private final int id;
    private final int categoryId;
    private final String text;
    private final String[] options; // index 0 = A, 1 = B, ...
    private final char correctOption;

    public Question(int id, int categoryId, String text, String[] options, char correctOption) {
        this.id = id;
        this.categoryId = categoryId;
        this.text = text;
        this.options = options;
        this.correctOption = Character.toUpperCase(correctOption);
    }

    public int getId() { return id; }
    public int getCategoryId() { return categoryId; }
    public String getText() { return text; }
    public String[] getOptions() { return options; }
    public char getCorrectOption() { return correctOption; }

    public boolean isCorrect(char answer) {
        return Character.toUpperCase(answer) == correctOption;
    }

    /** Text of the correct answer, e.g. "final". */
    public String getCorrectText() {
        return options[correctOption - 'A'];
    }
}
