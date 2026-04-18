package currencyconverter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/exchange")
public class ExchangeServlet extends HttpServlet {

    private static final String API_URL = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=11";
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        Map<String, Object> jsonResponse = new HashMap<>();

        String from = request.getParameter("fromCurrency");
        String to = request.getParameter("toCurrency");
        String rawAmount = request.getParameter("amount");

        try {
            if (from == null || to == null || rawAmount == null) {
                throw new IllegalArgumentException("Не всі параметри передані");
            }

            from = from.toUpperCase().trim();
            to = to.toUpperCase().trim();
            double amount = Double.parseDouble(rawAmount);

            if (amount < 0) throw new IllegalArgumentException("Сума не може бути від'ємною");

            Map<String, CurrencyRate> ratesMap = getRatesMap();

            if (!ratesMap.containsKey(from)) throw new IllegalArgumentException("Невідома валюта: " + from);
            if (!ratesMap.containsKey(to)) throw new IllegalArgumentException("Невідома валюта: " + to);

            double result = calculateExchange(amount, ratesMap.get(from).getBuyRate(), ratesMap.get(to).getSaleRate());

            jsonResponse.put("status", "success");
            jsonResponse.put("result", result);
            jsonResponse.put("message", String.format("%.2f %s = %.2f %s", amount, from, result, to));

            HistoryServlet.addOperation(new ExchangeOperation(from, to, amount, result));

        } catch (Exception e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Помилка: " + e.getMessage());
            HistoryServlet.addOperation(new ExchangeOperation(from, to, rawAmount, e.getMessage()));
        }

        mapper.writeValue(out, jsonResponse);
    }

    public static double calculateExchange(double amount, double rateFromToUah, double rateUahToResult) {
        double result = (amount * rateFromToUah) / rateUahToResult;
        return Math.round(result * 100.0) / 100.0;
    }

    private Map<String, CurrencyRate> getRatesMap() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).GET().build();
        HttpResponse<String> apiResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

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
}