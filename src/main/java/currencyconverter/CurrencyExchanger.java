package currencyconverter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CurrencyExchanger {

    private static final String API_URL = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=11";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Завантаження курсів...");

        try {
            List<CurrencyRate> listRates = getRates();

            // 2. Использование Map вместо прямого перебора
            Map<String, CurrencyRate> ratesMap = new HashMap<>();
            for (CurrencyRate rate : listRates) {
                ratesMap.put(rate.getCurrency(), rate);
            }

            boolean isRunning = true;
            while (isRunning) {
                System.out.println("\n1: Курси | 2: Обмін | exit: Вихід");
                System.out.print("Вибір: ");
                String choice = scanner.nextLine().trim();

                // 1. switch вместо if-else
                switch (choice) {
                    case "1":
                        for (CurrencyRate rate : ratesMap.values()) {
                            System.out.println(rate);
                        }
                        break;
                    case "2":
                        convertCurrency(scanner, ratesMap);
                        break;
                    case "exit":
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Невідома команда");
                }
            }
        } catch (Exception e) {
            System.err.println("Помилка: " + e.getMessage());
        }
    }

    private static List<CurrencyRate> getRates() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new ObjectMapper().readValue(response.body(), new TypeReference<>() {});
    }

    private static void convertCurrency(Scanner scanner, Map<String, CurrencyRate> rates) {
        System.out.print("Валюта, яку ВІДДАЄТЕ (UAH/USD/EUR): ");
        String from = scanner.nextLine().trim().toUpperCase();
        System.out.print("Валюта, яку ОТРИМУЄТЕ (UAH/USD/EUR): ");
        String to = scanner.nextLine().trim().toUpperCase();
        System.out.print("Сума: ");

        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());
            double result;

            // 3. Конвертация в обе стороны
            if (from.equals("UAH") && rates.containsKey(to)) {
                result = amount / rates.get(to).getSaleRate(); // Покупка у банка
            } else if (to.equals("UAH") && rates.containsKey(from)) {
                result = amount * rates.get(from).getBuyRate(); // Продажа банку
            } else if (rates.containsKey(from) && rates.containsKey(to)) {
                double fromRate = rates.get(from).getBuyRate();
                double toRate = rates.get(to).getSaleRate();
                result = (amount * fromRate) / toRate; // Кросс-курс
            } else {
                System.out.println("Помилка валюти");
                return;
            }

            System.out.printf("Результат: %.2f %s%n", result, to);
        } catch (NumberFormatException e) {
            System.out.println("Введіть число");
        }
    }
}