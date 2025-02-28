package org.kivislime.currencyexchange.model;

import java.util.Objects;

public class ExchangeRate {
    private final Long id;
    private final Currency baseCurrency;
    private final Currency targetCurrency;
    private final Double rate;

    public ExchangeRate(Long id, Currency baseCurrency, Currency targetCurrency, Double rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public Long getId() {
        return id;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getTargetCurrency() {
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

        ExchangeRate that = (ExchangeRate) obj;
        return Objects.equals(baseCurrency, that.baseCurrency) &&
                Objects.equals(targetCurrency, that.targetCurrency) &&
                Objects.equals(rate, that.rate);
    }

}

