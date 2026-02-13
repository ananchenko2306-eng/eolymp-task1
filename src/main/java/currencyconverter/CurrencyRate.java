package currencyconverter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
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

    public CurrencyRate() {}

    public String getCurrency() { return currency; }
    public String getBaseCurrency() { return baseCurrency; }
    public double getBuyRate() { return buyRate; }
    public double getSaleRate() { return saleRate; }

    public void setCurrency(String currency) { this.currency = currency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }
    public void setBuyRate(double buyRate) { this.buyRate = buyRate; }
    public void setSaleRate(double saleRate) { this.saleRate = saleRate; }

    @Override
    public String toString() {
        return String.format("%s: Купівля = %.4f, Продаж = %.4f", currency, buyRate, saleRate);
    }
}