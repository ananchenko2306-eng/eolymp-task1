package school_task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SchoolApp {

    static class SchoolClass {
        String name;
        public SchoolClass(String name) { this.name = name; }
    }

    static class Teacher {
        String name;
        public Teacher(String name) { this.name = name; }
    }

    static class Subject {
        String name;
        public Subject(String name) { this.name = name; }
    }


    static class Student {
        String name;
        SchoolClass schoolClass;

        public Student(String name, SchoolClass schoolClass) {
            this.name = name;
            this.schoolClass = schoolClass;
        }
    }

    static class ScheduleItem {
        String day;
        Student student;
        Subject subject;
        Teacher teacher;

        public ScheduleItem(String day, Student student, Subject subject, Teacher teacher) {
            this.day = day;
            this.student = student;
            this.subject = subject;
            this.teacher = teacher;
        }

        public void printInfo() {
            System.out.println("Ученик: " + student.name + " (Клас: " + student.schoolClass.name + ") " +
                    "| Предмет: " + subject.name + " (Преподаватель: " + teacher.name + ") " +
                    "| День: " + day);
        }
    }


    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/school_db";
        String user = "postgres";
        String password = "12345";

        List<ScheduleItem> fullSchedule = new ArrayList<>();

        System.out.println("Запуск программы... Подключение к школьной базе данных.\n");

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();

            String sqlQuery = "SELECT st.student_name, c.class_name, sub.subject_name, t.teacher_name, sch.day_of_week " +
                    "FROM students st " +
                    "JOIN classes c ON st.class_id = c.id " +
                    "JOIN schedule sch ON c.id = sch.class_id " +
                    "JOIN subjects sub ON sch.subject_id = sub.id " +
                    "JOIN teachers t ON sch.teacher_id = t.id;";

            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                String studentName = resultSet.getString("student_name");
                String className = resultSet.getString("class_name");
                String subjectName = resultSet.getString("subject_name");
                String teacherName = resultSet.getString("teacher_name");
                String day = resultSet.getString("day_of_week");

                SchoolClass classObj = new SchoolClass(className);
                Subject subjectObj = new Subject(subjectName);
                Teacher teacherObj = new Teacher(teacherName);

                Student studentObj = new Student(studentName, classObj);

                ScheduleItem item = new ScheduleItem(day, studentObj, subjectObj, teacherObj);

                fullSchedule.add(item);
            }

            resultSet.close();
            statement.close();
            connection.close();

            System.out.println("=== Расписание уроков для учеников ===\n");
            for (ScheduleItem item : fullSchedule) {
                item.printInfo();
            }

        } catch (Exception e) {
            System.out.println("Ошибка подключения! Проверь пароль, порт или название базы данных.");
            System.out.println("Детали ошибки: " + e.getMessage());
        }
    }
}