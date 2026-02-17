package currencyconverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
public class ExchangeOperation {
    private String time;
    private String fromCurrency;
    private String toCurrency;
    private double amountGiven;
    private double amountReceived;
    private String error;

    public ExchangeOperation() {}

    public ExchangeOperation(String fromCurrency, String toCurrency, double amountGiven, double amountReceived) {
        this.time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amountGiven = amountGiven;
        this.amountReceived = amountReceived;
        this.error = "Успіх";
    }

    public ExchangeOperation(String fromCurrency, String toCurrency, String inputValue, String errorMsg) {
        this.time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amountGiven = 0;
        this.amountReceived = 0;
        this.error = "Input: '" + inputValue + "' -> Error: " + errorMsg;
    }

    public String getTime() { return time; }
    public String getFromCurrency() { return fromCurrency; }
    public String getToCurrency() { return toCurrency; }
    public double getAmountGiven() { return amountGiven; }
    public double getAmountReceived() { return amountReceived; }
    public String getError() { return error; }
}