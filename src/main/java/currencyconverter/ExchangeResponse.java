package currencyconverter;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExchangeResponse {
    private String status;
    private String from;
    private String to;
    private Double amount;
    private Double result;
    private String message;

    public ExchangeResponse(String status, String from, String to, double amount, double result) {
        this.status = status;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.result = result;
    }

    public ExchangeResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() { return status; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public Double getAmount() { return amount; }
    public Double getResult() { return result; }
    public String getMessage() { return message; }
}