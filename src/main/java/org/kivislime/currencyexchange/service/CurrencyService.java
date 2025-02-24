package org.kivislime.currencyexchange.service;

import org.kivislime.currencyexchange.model.CurrencyDTO;

import java.util.List;

public interface CurrencyService {
    List<CurrencyDTO> getAllCurrencies();
}
