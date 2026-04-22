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

public class ExchangeService {
    private static final String API_URL = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=11";
    private static final String LOG_FILE_PATH = System.getProperty("user.home") + "/java_logs/operations.json";
    private final ObjectMapper mapper = new ObjectMapper();

    public ExchangeResponse performExchange(String from, String to, String rawAmount) throws Exception {
        if (from == null || to == null || rawAmount == null) {
            throw new IllegalArgumentException("Введіть всі параметри: from, to, amount");
        }

        from = from.toUpperCase().trim();
        to = to.toUpperCase().trim();
        double amount = Double.parseDouble(rawAmount);

        if (amount < 0) throw new IllegalArgumentException("Сума не може бути від'ємною");

        Map<String, CurrencyRate> ratesMap = fetchRates();

        if (!ratesMap.containsKey(from)) throw new IllegalArgumentException("Невідома валюта: " + from);
        if (!ratesMap.containsKey(to)) throw new IllegalArgumentException("Невідома валюта: " + to);

        double rateFromToUah = ratesMap.get(from).getBuyRate();
        double rateUahToResult = ratesMap.get(to).getSaleRate();

        double result = Math.round((amount * rateFromToUah / rateUahToResult) * 100.0) / 100.0;

        saveToLog(new ExchangeOperation(from, to, amount, result));
        return new ExchangeResponse("success", from, to, amount, result);
    }

    private Map<String, CurrencyRate> fetchRates() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<CurrencyRate> list = mapper.readValue(response.body(), new TypeReference<>() {});
        Map<String, CurrencyRate> map = new HashMap<>();
        for (CurrencyRate r : list) map.put(r.getCurrency(), r);

        CurrencyRate uah = new CurrencyRate();
        uah.setCurrency("UAH"); uah.setBuyRate(1.0); uah.setSaleRate(1.0);
        map.put("UAH", uah);

        return map;
    }

    public List<ExchangeOperation> getHistory() throws IOException {
        File file = new File(LOG_FILE_PATH);
        if (!file.exists() || file.length() == 0) return new ArrayList<>();
        return mapper.readValue(file, new TypeReference<List<ExchangeOperation>>() {});
    }

    public void saveToLog(ExchangeOperation op) {
        try {
            List<ExchangeOperation> history = getHistory();
            history.add(op);
            File file = new File(LOG_FILE_PATH);
            file.getParentFile().mkdirs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, history);
        } catch (IOException e) {
            System.err.println("Помилка запису логу: " + e.getMessage());
        }
    }
}
