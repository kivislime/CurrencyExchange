package org.kivislime.currencyexchange.service;

import org.kivislime.currencyexchange.model.CurrencyCreationDTO;
import org.kivislime.currencyexchange.model.CurrencyDTO;

import java.util.List;

public interface CurrencyService {
    List<CurrencyDTO> getAllCurrencies();

    boolean currencyExists(String currency);

    CurrencyDTO addCurrency(CurrencyCreationDTO currency);

    CurrencyDTO getCurrency(String currency);
}
