package org.kivislime.currencyexchange.service;

import org.kivislime.currencyexchange.model.Currency;
import org.kivislime.currencyexchange.model.CurrencyCreationDTO;
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

    @Override
    public boolean currencyExists(String currency) {
        return currencyDao.currencyExists(currency);
    }

    @Override
    public CurrencyDTO addCurrency(CurrencyCreationDTO currencyCreationDTO) {
        Currency currencyToAdd = convertToCurrency(currencyCreationDTO);
        Currency currencyResult = currencyDao.addCurrency(currencyToAdd);
        return convertToDTO(currencyResult);
    }

    @Override
    public CurrencyDTO getCurrency(String currency) {
        return currencyDao.getCurrency(currency)
                .map(this::convertToDTO)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency " + currency + " not found"));
    }


    private CurrencyDTO convertToDTO(Currency currency) {
        return new CurrencyDTO(currency.getId(), currency.getCode(), currency.getFullName(), currency.getSign());
    }

    private Currency convertToCurrency(CurrencyCreationDTO currencyCreationDTO) {
        return new Currency(null, currencyCreationDTO.getCode(), currencyCreationDTO.getFullName(), currencyCreationDTO.getSign());
    }
}
