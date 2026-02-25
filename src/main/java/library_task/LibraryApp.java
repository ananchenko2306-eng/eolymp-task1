package library_task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class LibraryApp {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/library_db";
        String user = "postgres";
        String password = "12345";

        System.out.println("Запуск приложения... Подключаемся к базе данных.");

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();

            String sqlQuery = "SELECT readers.name AS reader, books.title AS book, authors.name AS author, book_loans.loan_date " +
                    "FROM book_loans " +
                    "JOIN readers ON book_loans.reader_id = readers.id " +
                    "JOIN books ON book_loans.book_id = books.id " +
                    "JOIN authors ON books.author_id = authors.id;";

            ResultSet resultSet = statement.executeQuery(sqlQuery);

            System.out.println("=== Список выданных книг ===");

            while (resultSet.next()) {
                String readerName = resultSet.getString("reader");
                String bookTitle = resultSet.getString("book");
                String authorName = resultSet.getString("author");
                String date = resultSet.getString("loan_date");

                System.out.println("Читатель(ница): " + readerName +
                        " | Взял(а) книгу: '" + bookTitle +
                        "' (Автор(ка): " + authorName +
                        ") | Дата: " + date);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            System.out.println("Ошибка подключения! Проверьте пароль или название базы.");
            e.printStackTrace();
        }
    }
}