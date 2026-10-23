-- Quiz App schema + sample data
CREATE DATABASE IF NOT EXISTS quiz_db CHARACTER SET utf8mb4;
USE quiz_db;

DROP TABLE IF EXISTS results;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(64)  NOT NULL,
    salt          VARCHAR(32)  NOT NULL,
    role          ENUM('PLAYER','ADMIN') NOT NULL DEFAULT 'PLAYER',
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE questions (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    category_id    INT          NOT NULL,
    question_text  VARCHAR(255) NOT NULL,
    option_a       VARCHAR(120) NOT NULL,
    option_b       VARCHAR(120) NOT NULL,
    option_c       VARCHAR(120) NOT NULL,
    option_d       VARCHAR(120) NOT NULL,
    correct_option CHAR(1)      NOT NULL,
    CONSTRAINT chk_correct CHECK (correct_option IN ('A','B','C','D')),
    CONSTRAINT fk_q_cat FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE TABLE results (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT       NOT NULL,
    category_id INT       NOT NULL,
    score       INT       NOT NULL,
    total       INT       NOT NULL,
    taken_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_r_user FOREIGN KEY (user_id)     REFERENCES users(id)      ON DELETE CASCADE,
    CONSTRAINT fk_r_cat  FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

INSERT INTO categories (name) VALUES ('Java'), ('SQL'), ('Computer Basics');

-- Java
INSERT INTO questions (category_id, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES
((SELECT id FROM categories WHERE name='Java'), 'Which keyword prevents a class from being subclassed?', 'static', 'final', 'abstract', 'private', 'B'),
((SELECT id FROM categories WHERE name='Java'), 'What is the default value of an int instance variable?', '0', 'null', 'undefined', '-1', 'A'),
((SELECT id FROM categories WHERE name='Java'), 'Which collection does NOT allow duplicate elements?', 'ArrayList', 'LinkedList', 'HashSet', 'Vector', 'C'),
((SELECT id FROM categories WHERE name='Java'), 'Which method is the entry point of a Java program?', 'start()', 'main()', 'run()', 'init()', 'B'),
((SELECT id FROM categories WHERE name='Java'), 'Which of these is NOT a primitive type?', 'int', 'boolean', 'String', 'char', 'C'),
((SELECT id FROM categories WHERE name='Java'), 'What does JVM stand for?', 'Java Virtual Machine', 'Java Variable Method', 'Joint Virtual Module', 'Java Verified Machine', 'A');

-- SQL
INSERT INTO questions (category_id, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES
((SELECT id FROM categories WHERE name='SQL'), 'Which clause filters rows before grouping?', 'WHERE', 'HAVING', 'GROUP BY', 'LIMIT', 'A'),
((SELECT id FROM categories WHERE name='SQL'), 'Which statement empties a table and resets AUTO_INCREMENT?', 'DROP', 'TRUNCATE', 'REMOVE', 'CUT', 'B'),
((SELECT id FROM categories WHERE name='SQL'), 'Which clause sorts the result set?', 'SORT', 'GROUP BY', 'ORDER BY', 'ARRANGE', 'C'),
((SELECT id FROM categories WHERE name='SQL'), 'Which join returns only rows that match in both tables?', 'LEFT JOIN', 'INNER JOIN', 'RIGHT JOIN', 'CROSS JOIN', 'B'),
((SELECT id FROM categories WHERE name='SQL'), 'Which aggregate function counts rows?', 'SUM()', 'AVG()', 'COUNT()', 'MAX()', 'C');

-- Computer Basics
INSERT INTO questions (category_id, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES
((SELECT id FROM categories WHERE name='Computer Basics'), 'What is decimal 5 in binary?', '100', '101', '110', '111', 'B'),
((SELECT id FROM categories WHERE name='Computer Basics'), 'Which data structure follows LIFO order?', 'Queue', 'Stack', 'Array', 'Tree', 'B'),
((SELECT id FROM categories WHERE name='Computer Basics'), 'What is the time complexity of binary search?', 'O(n)', 'O(n^2)', 'O(log n)', 'O(1)', 'C'),
((SELECT id FROM categories WHERE name='Computer Basics'), 'What does HTTP stand for?', 'HyperText Transfer Protocol', 'High Transfer Text Process', 'Hyper Terminal Transfer Program', 'Host Text Transport Protocol', 'A'),
((SELECT id FROM categories WHERE name='Computer Basics'), 'Which of these is NOT an operating system?', 'Linux', 'Windows', 'MySQL', 'macOS', 'C');
