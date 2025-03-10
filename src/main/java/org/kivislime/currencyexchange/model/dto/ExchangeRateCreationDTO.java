package org.kivislime.currencyexchange.model.dto;

import java.util.Objects;

public class ExchangeRateCreationDTO {
    private final String baseCurrency;
    private final String targetCurrency;
    private final String rate;

    public ExchangeRateCreationDTO(String baseCurrency, String targetCurrency, String rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public String getRate() {
        return rate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrency, targetCurrency, rate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ExchangeRateCreationDTO that = (ExchangeRateCreationDTO) obj;
        return Objects.equals(baseCurrency, that.baseCurrency) &&
                Objects.equals(targetCurrency, that.targetCurrency) &&
                Objects.equals(rate, that.rate);
    }

}
