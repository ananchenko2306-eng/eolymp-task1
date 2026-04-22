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

@WebServlet("/api/exchange")
public class ExchangeServlet extends HttpServlet {

    private static final String API_URL = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=11";
    private static final String LOG_FILE_PATH = System.getProperty("user.home") + "/java_logs/operations.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String rawAmount = request.getParameter("amount");

        try {
            if (from == null || to == null || rawAmount == null) {
                throw new IllegalArgumentException("Введіть всі параметри: from, to, amount");
            }

            from = from.toUpperCase().trim();
            to = to.toUpperCase().trim();

            Map<String, CurrencyRate> ratesMap = getRatesMap();

            if (!ratesMap.containsKey(from)) throw new IllegalArgumentException("Невідома валюта: " + from);
            if (!ratesMap.containsKey(to)) throw new IllegalArgumentException("Невідома валюта: " + to);

            double amount = Double.parseDouble(rawAmount);
            if (amount < 0) throw new IllegalArgumentException("Сума не може бути від'ємною");

            double rateFromToUah = ratesMap.get(from).getBuyRate();
            double rateUahToResult = ratesMap.get(to).getSaleRate();

            double result = (amount * rateFromToUah) / rateUahToResult;
            result = Math.round(result * 100.0) / 100.0;

            saveOperationToJson(new ExchangeOperation(from, to, amount, result));

            out.print("{\"status\": \"success\", \"from\": \"" + from + "\", \"to\": \"" + to + "\", \"amount\": " + amount + ", \"result\": " + result + "}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
            saveOperationToJson(new ExchangeOperation(from, to, rawAmount, e.getMessage()));
        }
        out.flush();
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