package org.kivislime.currencyexchange.model;

import java.util.Objects;

public class ExchangeRateCreationDTO {
    private final CurrencyDTO baseCurrency;
    private final CurrencyDTO targetCurrency;
    private final Double rate;

    public ExchangeRateCreationDTO(CurrencyDTO baseCurrency, CurrencyDTO targetCurrency, Double rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public CurrencyDTO getBaseCurrency() {
        return baseCurrency;
    }

    public CurrencyDTO getTargetCurrency() {
        return targetCurrency;
    }

    public Double getRate() {
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
