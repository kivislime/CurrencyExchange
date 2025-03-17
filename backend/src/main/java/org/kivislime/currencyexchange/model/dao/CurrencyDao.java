package org.kivislime.currencyexchange.model.dao;

import org.kivislime.currencyexchange.model.domain.Currency;
import org.kivislime.currencyexchange.model.domain.ExchangeRate;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public interface CurrencyDao{
    Set<Currency> getCurrencies();

    boolean currencyExists(String currencyCode);

    Optional<Currency> getCurrency(String currencyCode);

    Currency addCurrency(Currency currency);

    Set<ExchangeRate> getAllExchangeRates();

    Optional<ExchangeRate> getExchangeRateByPair(Currency firsCurrency, Currency secondCurrency);

    ExchangeRate addExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate);

    ExchangeRate patchExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate);

    Optional<BigDecimal> getRate(Long baseCurrencyId, Long targetCurrencyId);

    Set<Long> getIntersectIds(Currency baseCurrency, Currency targetCurrency);
}
