package currencyconverter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyRate {

    @JsonProperty("ccy")
    private String currency;

    @JsonProperty("base_ccy")
    private String baseCurrency;

    @JsonProperty("buy")
    private double buyRate;

    @JsonProperty("sale")
    private double saleRate;

    public CurrencyRate() {
    }

    public String getCurrency() {
        return currency;
    }

    public double getBuyRate() {
        return buyRate;
    }

    public double getSaleRate() {
        return saleRate;
    }

    @Override
    public String toString() {
        return "Валюта: " + currency + " -> Купівля: " + buyRate + ", Продаж: " + saleRate;
    }
}