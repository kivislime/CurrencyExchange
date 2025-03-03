package org.kivislime.currencyexchange.service;

import org.kivislime.currencyexchange.model.CurrencyCreationDTO;
import org.kivislime.currencyexchange.model.CurrencyDTO;
import org.kivislime.currencyexchange.model.ExchangeRateDTO;

import java.util.Set;

public interface CurrencyService {
    Set<CurrencyDTO> getAllCurrencies();

    boolean currencyExists(String currency);

    CurrencyDTO addCurrency(CurrencyCreationDTO currency);

    CurrencyDTO getCurrency(String currency);

    Set<ExchangeRateDTO> getAllExchangeRates();

    ExchangeRateDTO getExchangeRateByPair(String pathInfo);
}
