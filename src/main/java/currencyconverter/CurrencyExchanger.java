package currencyconverter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Scanner;

public class CurrencyExchanger {

    private static final String API_URL = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=11";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Оновлення актуальних курсів");

        try {
            List<CurrencyRate> rates = getRates();

            while (true) {
                System.out.println("\n=== ВИБІР ОПЦІЇ ===");
                System.out.println("1: Дізнатися поточний курс");
                System.out.println("2: Конвертувати валюту");
                System.out.println("exit: Вийти");
                System.out.print("Ваш вибір: ");

                String choice = scanner.nextLine().trim();

                if (choice.equals("1")) {
                    System.out.println("\n=== Курси валют (ПриватБанк) ===");
                    for (CurrencyRate rate : rates) {
                        System.out.println(rate);
                    }
                } else if (choice.equals("2")) {
                    convertCurrency(scanner, rates);
                } else if (choice.equalsIgnoreCase("exit")) {
                    System.out.println("Роботу завершено.");
                    break;
                } else {
                    System.out.println("Невідома команда.");
                }
            }

        } catch (Exception e) {
            System.err.println("Помилка: " + e.getMessage());
        }
    }

    private static List<CurrencyRate> getRates() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.body(), new TypeReference<>() {
        });
    }

    private static void convertCurrency(Scanner scanner, List<CurrencyRate> rates) {
        System.out.println("Введіть валюту (EUR або USD):");
        String code = scanner.nextLine().trim().toUpperCase();

        CurrencyRate selected = null;
        for (CurrencyRate rate : rates) {
            if (rate.getCurrency().equals(code)) {
                selected = rate;
                break;
            }
        }

        if (selected == null) {
            System.out.println("Валюту не знайдено.");
            return;
        }

        System.out.println("Введіть суму у " + code + ":");
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());
            double result = amount * selected.getBuyRate();
            System.out.printf("Результат: %.2f UAH%n", result);
        } catch (NumberFormatException e) {
            System.out.println("Помилка: введіть число.");
        }
    }
}
