package org.kivislime.currencyexchange.model;

import java.math.BigDecimal;
import java.util.Objects;

public class ExchangeRateDTO {
    private final Long id;
    private final CurrencyDTO baseCurrency;
    private final CurrencyDTO targetCurrency;
    private final BigDecimal rate;

    public ExchangeRateDTO(Long id, CurrencyDTO baseCurrency, CurrencyDTO targetCurrency, BigDecimal rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public Long getId() {
        return id;
    }

    public CurrencyDTO getBaseCurrency() {
        return baseCurrency;
    }

    public CurrencyDTO getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseCurrency, targetCurrency, rate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ExchangeRateDTO that = (ExchangeRateDTO) obj;
        return Objects.equals(id, that.id) &&
                Objects.equals(baseCurrency, that.baseCurrency) &&
                Objects.equals(targetCurrency, that.targetCurrency) &&
                Objects.equals(rate, that.rate);
    }

}

