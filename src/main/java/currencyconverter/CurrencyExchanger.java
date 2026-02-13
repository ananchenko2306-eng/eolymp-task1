package currencyconverter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CurrencyExchanger {

    private static final String API_URL = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=11";

    private static final String LOG_FILE_PATH = "C:\\java_logs\\operations.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Завантаження курсів...");

        try {
            List<CurrencyRate> listRates = getRates();

            Map<String, CurrencyRate> ratesMap = new HashMap<>();
            for (CurrencyRate rate : listRates) {
                ratesMap.put(rate.getCurrency(), rate);
            }

            boolean isRunning = true;
            while (isRunning) {
                System.out.println("\n1: Курси | 2: Обмін | exit: Вихід");
                System.out.print("Вибір: ");
                String choice = scanner.nextLine().trim();

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
        return mapper.readValue(response.body(), new TypeReference<>() {});
    }

    private static void convertCurrency(Scanner scanner, Map<String, CurrencyRate> rates) {
        System.out.print("Валюта, яку ВІДДАЄТЕ (UAH/USD/EUR): ");
        String from = scanner.nextLine().trim().toUpperCase();
        System.out.print("Валюта, яку ОТРИМУЄТЕ (UAH/USD/EUR): ");
        String to = scanner.nextLine().trim().toUpperCase();
        System.out.print("Сума: ");

        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());

            boolean isFromValid = from.equals("UAH") || rates.containsKey(from);
            boolean isToValid = to.equals("UAH") || rates.containsKey(to);

            if (!isFromValid || !isToValid) {
                System.out.println("Помилка: Введено невідому валюту.");
                return;
            }

            double rateFromToUah = from.equals("UAH") ? 1.0 : rates.get(from).getBuyRate();
            double rateUahToResult = to.equals("UAH") ? 1.0 : rates.get(to).getSaleRate();

            double result = (amount * rateFromToUah) / rateUahToResult;
            result = Math.round(result * 100.0) / 100.0;

            System.out.printf("Результат: %.2f %s%n", result, to);

            saveOperationToJson(from, to, amount, result);

        } catch (NumberFormatException e) {
            System.out.println("Введіть число");
        }
    }

    private static void saveOperationToJson(String from, String to, double amount, double result) {
        File file = new File(LOG_FILE_PATH);
        List<ExchangeOperation> history = new ArrayList<>();

        try {
            if (file.exists() && file.length() > 0) {
                history = mapper.readValue(file, new TypeReference<>() {
                });
            }

            ExchangeOperation newOp = new ExchangeOperation(from, to, amount, result);
            history.add(newOp);

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, history);
            System.out.println("[Інфо: Операцію успішно збережено у файл JSON]");

        } catch (IOException e) {
            System.err.println("Помилка збереження в JSON: " + e.getMessage());
        }
    }
}