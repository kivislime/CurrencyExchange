package org.kivislime.currencyexchange.service;

import org.kivislime.currencyexchange.model.Currency;
import org.kivislime.currencyexchange.model.CurrencyDTO;
import org.kivislime.currencyexchange.model.CurrencyDao;

import java.util.ArrayList;
import java.util.List;

public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyDao currencyDao;

    public CurrencyServiceImpl(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    @Override
    public List<CurrencyDTO> getAllCurrencies() {
        List<Currency> currencies = currencyDao.getCurrencies();
        List<CurrencyDTO> dtos = new ArrayList<>();
        for (Currency currency : currencies) {
            dtos.add(convertToDTO(currency));
        }
        return dtos;
    }

    private CurrencyDTO convertToDTO(Currency currency) {
        return new CurrencyDTO(currency.getCode(), currency.getFullName(), currency.getSign());
    }
}
