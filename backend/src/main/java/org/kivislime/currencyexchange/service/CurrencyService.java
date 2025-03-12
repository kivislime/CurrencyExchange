package org.kivislime.currencyexchange.service;

import org.kivislime.currencyexchange.model.dto.*;

import java.util.Set;

public interface CurrencyService {
    Set<CurrencyDTO> getAllCurrencies();

    boolean currencyExists(String currency);

    CurrencyDTO addCurrency(CurrencyCreationDTO currency);

    CurrencyDTO getCurrency(String currency);

    Set<ExchangeRateDTO> getAllExchangeRates();

    ExchangeRateDTO getExchangeRateByPair(String pathInfo);

    ExchangeRateDTO addExchangeRate(ExchangeRateCreationDTO exchangeRateCreationDTO);

    ExchangeRateDTO patchExchangeRate(String getPathInfo, String rate);

    ExchangeResultDTO exchange(ExchangeResultCreationDTO exchangeResultCreationDTO);
}
