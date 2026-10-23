# Console Quiz Application (Java + MySQL)

A menu-driven quiz game for the terminal. Players register, log in, take 5-question
multiple-choice quizzes, see their history and a leaderboard. Admins can add questions.

**Tech:** Java 17+, MySQL 8, JDBC, Maven. **Level:** Intermediate.

## Project structure

```
QuizApp/
├── pom.xml                         Maven build file (dependencies + run plugin)
├── sql/
│   └── schema.sql                  Creates database, tables, and sample questions
└── src/main/
    ├── resources/
    │   └── db.properties           DB URL / username / password  (EDIT THIS)
    └── java/com/quizapp/
        ├── Main.java               Entry point + menus
        ├── model/                  Plain data classes
        │   ├── User.java
        │   ├── Category.java
        │   ├── Question.java
        │   └── QuizResult.java
        ├── db/
        │   └── DBConnection.java   Opens JDBC connections
        ├── dao/                    SQL lives ONLY here (Data Access Objects)
        │   ├── UserDAO.java
        │   ├── CategoryDAO.java
        │   ├── QuestionDAO.java
        │   └── ResultDAO.java
        ├── service/
        │   └── QuizService.java    Quiz flow, scoring, leaderboard, admin logic
        └── util/
            ├── InputHelper.java    Safe keyboard input (no crashes on bad input)
            └── PasswordUtil.java   Salted SHA-256 password hashing
```

## Database design

```
users(id, username UNIQUE, password_hash, salt, role, created_at)
categories(id, name UNIQUE)
questions(id, category_id -> categories, question_text, option_a..d, correct_option)
results(id, user_id -> users, category_id -> categories, score, total, taken_at)
```

## Setup & run

1. **Install** JDK 17+, Maven 3.8+, and MySQL 8. Check with `java -version`, `mvn -v`, `mysql --version`.
2. **Create the database** (from the project folder):
   ```
   mysql -u root -p < sql/schema.sql
   ```
   (Warning: the script drops and recreates the quiz tables.)
3. **Configure** `src/main/resources/db.properties` - set `db.user` and `db.password`.
4. **Run:**
   ```
   mvn compile exec:java
   ```
   The first run downloads the MySQL driver automatically.

### Make yourself an admin (optional)
Register a user in the app, then:
```
mysql -u root -p quiz_db -e "UPDATE users SET role='ADMIN' WHERE username='alice';"
```
Log in again and menu option 4 appears.

### Troubleshooting
| Problem | Fix |
|---|---|
| `Access denied for user` | Wrong `db.user` / `db.password` in db.properties |
| `Unknown database 'quiz_db'` | Run step 2 |
| `Communications link failure` | MySQL isn't running, or the port is not 3306 |
| `No categories found` | schema.sql didn't finish; re-run it |
| Weird input behaviour under `mvn exec:java` | Run in a real terminal, not an IDE console that doesn't pass stdin |

## Sample output

```
=================================
   CONSOLE QUIZ APPLICATION
=================================

1. Login
2. Register
3. Exit
Choose: 2

--- Register ---
Username: alice
Password (min 4 chars): secret
Account created! You can log in now.

1. Login
2. Register
3. Exit
Choose: 1

--- Login ---
Username: alice
Password: secret

Welcome, alice!

--- Main Menu ---
1. Take a quiz
2. My history
3. Leaderboard
0. Logout
Choose: 1

Choose a category:
  1. Computer Basics
  2. Java
  3. SQL
  0. Back
Your choice: 2

=== Java Quiz (5 questions) ===

Q1. Which collection does NOT allow duplicate elements?
   A) ArrayList
   B) LinkedList
   C) HashSet
   D) Vector
Your answer (A-D): c
  Correct!

Q2. What is the default value of an int instance variable?
   A) 0
   B) null
   C) undefined
   D) -1
Your answer (A-D): b
  Wrong. Correct answer: A) 0

... (3 more questions) ...

----------------------------------
 Player   : alice
 Category : Java
 Score    : 4 / 5 (80%)
 Result   : Good job!
----------------------------------

--- Main Menu ---
Choose: 3

=== Leaderboard (Top 10) ===
#    Player          Category          Score    Percent
1    alice           Java              4/5      80%
```

## How it works (beginner-friendly)

**Layered design.** Each folder has one job, so you can change one part without breaking the others:

| Layer | Job | Knows about SQL? |
|---|---|---|
| `Main` | Shows menus, reads choices | No |
| `service` | Rules of the quiz (ask, score, rank) | No |
| `dao` | Talks to MySQL | **Yes** |
| `model` | Holds data (a Question, a User...) | No |

**JDBC in 4 steps** (see any DAO):
1. `DBConnection.getConnection()` opens a connection.
2. `prepareStatement("... WHERE id = ?")` prepares SQL with `?` placeholders.
3. `ps.setInt(1, value)` fills the placeholders, then `executeQuery()` / `executeUpdate()` runs it.
4. Read rows with `while (rs.next()) { rs.getString("col") }`.

**Why `PreparedStatement`?** Values are sent separately from the SQL text, which blocks
*SQL injection* (a user typing `' OR '1'='1` as a username).

**Why `try-with-resources`?** `try (Connection con = ...) { }` automatically closes the
connection, statement and result set, even if an exception happens. No leaks.

**Password safety.** We never store the password. On register we make a random *salt*,
store `SHA-256(salt + password)`, and on login we recompute and compare. (Real apps should
use bcrypt or Argon2; SHA-256 is used here to avoid extra libraries.)

**Random questions.** `ORDER BY RAND() LIMIT 5` makes MySQL shuffle and take 5. Fine for
small tables; for huge tables you'd use a different technique.

**Leaderboard.** Sorted by `score / total` so a 4/5 beats a 7/10, with earlier attempts winning ties.

**Input validation.** `InputHelper` loops until the user gives valid input, so typing
`abc` where a number is expected never crashes the program.

**Error handling.** DAOs `throw SQLException`; `Main` catches it, prints a friendly message,
and keeps running.

## Ideas to extend

- Timer per question; difficulty levels; delete/edit questions (admin).
- Replace SHA-256 with BCrypt; use a connection pool (HikariCP).
- Add JUnit tests for `Question.isCorrect` and `PasswordUtil`.
- Package as a runnable jar with the Maven Shade plugin.
