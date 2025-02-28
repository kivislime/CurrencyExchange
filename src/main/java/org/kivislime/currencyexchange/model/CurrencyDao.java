package org.kivislime.currencyexchange.model;

import java.util.List;
import java.util.Optional;

public interface CurrencyDao{
    List<Currency> getCurrencies();

    boolean currencyExists(String currency);

    Optional<Currency> getCurrency(String currency);

    Currency addCurrency(Currency currency);
}
