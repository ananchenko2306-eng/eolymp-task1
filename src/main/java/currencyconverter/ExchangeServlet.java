package currencyconverter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private static final String API_URL = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=11";
    private static final String LOG_FILE_PATH = System.getProperty("user.home") + "/java_logs/operations.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String from = request.getParameter("fromCurrency").toUpperCase().trim();
        String to = request.getParameter("toCurrency").toUpperCase().trim();
        String rawAmount = request.getParameter("amount");

        out.println("<html><body style='font-family: Arial; margin: 40px;'>");
        out.println("<h2>Результат обміну:</h2>");

        try {
            Map<String, CurrencyRate> ratesMap = getRatesMap();

            if (!ratesMap.containsKey(from)) throw new IllegalArgumentException("Невідома валюта: " + from);
            if (!ratesMap.containsKey(to)) throw new IllegalArgumentException("Невідома валюта: " + to);

            double amount = Double.parseDouble(rawAmount);
            if (amount < 0) throw new IllegalArgumentException("Сума не може бути від'ємною");

            double rateFromToUah = ratesMap.get(from).getBuyRate();
            double rateUahToResult = ratesMap.get(to).getSaleRate();

            double result = (amount * rateFromToUah) / rateUahToResult;
            result = Math.round(result * 100.0) / 100.0;

            out.printf("<h3 style='color: green;'>%.2f %s = %.2f %s</h3>", amount, from, result, to);

            saveOperationToJson(new ExchangeOperation(from, to, amount, result));

        } catch (Exception e) {
            out.println("<h3 style='color: red;'>Помилка: " + e.getMessage() + "</h3>");
            saveOperationToJson(new ExchangeOperation(from, to, rawAmount, e.getMessage()));
        }

        out.println("<br><a href='index.html'>Повернутися назад</a>");
        out.println("</body></html>");
    }

    private Map<String, CurrencyRate> getRatesMap() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).GET().build();
        HttpResponse<String> apiResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<CurrencyRate> listRates = mapper.readValue(apiResponse.body(), new TypeReference<>() {});
        Map<String, CurrencyRate> ratesMap = new HashMap<>();

        for (CurrencyRate rate : listRates) {
            ratesMap.put(rate.getCurrency(), rate);
        }

        CurrencyRate uahRate = new CurrencyRate();
        uahRate.setCurrency("UAH");
        uahRate.setBaseCurrency("UAH");
        uahRate.setBuyRate(1.0);
        uahRate.setSaleRate(1.0);
        ratesMap.put("UAH", uahRate);

        return ratesMap;
    }

    private void saveOperationToJson(ExchangeOperation operation) {
        File file = new File(LOG_FILE_PATH);
        file.getParentFile().mkdirs();

        List<ExchangeOperation> history = new ArrayList<>();
        try {
            if (file.exists() && file.length() > 0) {
                history = mapper.readValue(file, new TypeReference<>() {});
            }
            history.add(operation);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, history);
        } catch (IOException e) {
            System.err.println("Помилка запису: " + e.getMessage());
        }
    }
}