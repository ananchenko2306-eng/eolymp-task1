DROP TABLE IF EXISTS schedule;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS teachers;
DROP TABLE IF EXISTS subjects;
DROP TABLE IF EXISTS classes;

CREATE TABLE classes (
    id SERIAL PRIMARY KEY,
    class_name VARCHAR(50) NOT NULL
);

CREATE TABLE subjects (
    id SERIAL PRIMARY KEY,
    subject_name VARCHAR(100) NOT NULL
);

CREATE TABLE teachers (
    id SERIAL PRIMARY KEY,
    teacher_name VARCHAR(100) NOT NULL
);

CREATE TABLE students (
    id SERIAL PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL,
    class_id INT REFERENCES classes(id)
);

CREATE TABLE schedule (
    id SERIAL PRIMARY KEY,
    day_of_week VARCHAR(20) NOT NULL,
    class_id INT REFERENCES classes(id),
    subject_id INT REFERENCES subjects(id),
    teacher_id INT REFERENCES teachers(id)
);


INSERT INTO classes (class_name) VALUES
('9-А'), ('9-Б'), ('9-В'), ('10-А'),
('10-Б'), ('10-В'), ('11-А'), ('11-Б'), ('11-В'), ('11-Г');

INSERT INTO subjects (subject_name) VALUES
('Математика'), ('История'), ('Физика'), ('Химия'),
('Английский'), ('Литература'), ('Биология'), ('География'), ('Украинский'), ('Информатика');

INSERT INTO teachers (teacher_name) VALUES
('Снегирева Вера Максимовна'), ('Цветков Владимир Иванович'), ('Попова Полина Кирилловна'),
('Широков Иван Леонидович'), ('Тарасова Наталья Игоревна'), ('Соколов Виктор Павлович'), ('Романова Елена Сергеевна'),
('Муратов Дмитрий Александрович'), ('Михайлова Татьяна Владимировна'), ('Лебедев Михаил Юрьевич');

INSERT INTO students (student_name, class_id) VALUES
('Александр Волков', 1), ('Екатерина Морозова', 2), ('Дмитрий Белов', 3), ('Анна Соколова', 4),
('Максим Кравченко', 5), ('Зайцева София', 6), ('Королева Дарья', 7), ('Мальцева Александра', 8),
('Михеев Андрей', 9), ('Серова Елизавета', 10);

INSERT INTO schedule (day_of_week, class_id, subject_id, teacher_id) VALUES
('Понедельник', 7, 1, 1),
('Понедельник', 7, 2, 2),
('Вторник', 1, 6, 6),
('Вторник', 2, 7, 7),
('Среда', 4, 8, 8),
('Среда', 8, 5, 5),
('Четверг', 9, 4, 4),
('Четверг', 10, 3, 3),
('Пятница', 5, 9, 9),
('Пятница', 6, 10, 10);