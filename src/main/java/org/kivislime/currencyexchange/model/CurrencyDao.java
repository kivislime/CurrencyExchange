package org.kivislime.currencyexchange.model;

import java.util.Optional;
import java.util.Set;

public interface CurrencyDao{
    Set<Currency> getCurrencies();

    boolean currencyExists(String currency);

    Optional<Currency> getCurrency(String currency);

    Currency addCurrency(Currency currency);

    Set<ExchangeRate> getAllExchangeRates();

    Optional<ExchangeRate> getExchangeRateByPair(Currency firsCurrency, Currency secondCurrency);
}
