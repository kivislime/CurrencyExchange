package org.kivislime.currencyexchange.model;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public interface CurrencyDao{
    Set<Currency> getCurrencies();

    boolean currencyExists(String currency);

    Optional<Currency> getCurrency(String currency);

    Currency addCurrency(Currency currency);

    Set<ExchangeRate> getAllExchangeRates();

    Optional<ExchangeRate> getExchangeRateByPair(Currency firsCurrency, Currency secondCurrency);

    boolean exchangeRateExists(Long id, Long id1);

    ExchangeRate addExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate);

    ExchangeRate patchExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate);

    Set<Long> getExchangeableCurrencyIdsForCurrency(Long id);

    Optional<BigDecimal> getRate(Long baseCurrencyId, Long targetCurrencyId);
}
