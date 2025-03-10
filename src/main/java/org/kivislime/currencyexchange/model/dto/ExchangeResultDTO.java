package org.kivislime.currencyexchange.model.dto;

import org.kivislime.currencyexchange.model.domain.Currency;

import java.math.BigDecimal;
import java.util.Objects;

public class ExchangeResultDTO {

    private final Currency baseCurrency;
    private final Currency targetCurrency;
    private final BigDecimal rate;
    private final BigDecimal amount;
    private final BigDecimal convertedAmount;

    public ExchangeResultDTO(Currency baseCurrency, Currency targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeResultDTO that = (ExchangeResultDTO) o;
        return Objects.equals(baseCurrency, that.baseCurrency) &&
                Objects.equals(targetCurrency, that.targetCurrency) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(rate, that.rate) &&
                Objects.equals(convertedAmount, that.convertedAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrency, targetCurrency, amount, rate, convertedAmount);
    }

}