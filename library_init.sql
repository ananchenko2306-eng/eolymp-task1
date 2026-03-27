DROP TABLE IF EXISTS book_loans;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS readers;

CREATE TABLE authors (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE readers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author_id INT REFERENCES authors(id)
);

CREATE TABLE book_loans (
    id SERIAL PRIMARY KEY,
    book_id INT REFERENCES books(id),
    reader_id INT REFERENCES readers(id),
    loan_date DATE
);

INSERT INTO authors (name) VALUES
('Джордж Оруэлл'),
('Стивен Кинг'),
('Эмили Бронте'),
('Тесс Герритсен'),
('Агата Кристи');

INSERT INTO readers (name) VALUES
('Беляева Екатерина'),
('Зуев Антон'),
('Исаева Марина'),
('Миронов Михаил'),
('Никифорова София');

INSERT INTO books (title, author_id) VALUES
('1984', 1),
('Мистер Мерседес', 2),
('Грозовой перевал', 3),
('Хирург', 4),
('Убийство в Восточном экспрессе', 5);

INSERT INTO book_loans (book_id, reader_id, loan_date) VALUES
(1, 1, '2026-02-13'),
(2, 2, '2026-02-16'),
(3, 3, '2026-02-19'),
(4, 4, '2026-02-22'),
(5, 5, '2026-02-25');