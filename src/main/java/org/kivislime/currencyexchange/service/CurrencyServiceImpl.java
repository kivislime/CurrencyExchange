package org.kivislime.currencyexchange.service;

import org.kivislime.currencyexchange.model.*;

import java.util.HashSet;
import java.util.Set;

public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyDao currencyDao;

    public CurrencyServiceImpl(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    @Override
    public Set<CurrencyDTO> getAllCurrencies() {
        Set<Currency> currencies = currencyDao.getCurrencies();
        Set<CurrencyDTO> dtos = new HashSet<>();
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

    @Override
    public Set<ExchangeRateDTO> getAllExchangeRates() {
        Set<ExchangeRate> exchangeRates = currencyDao.getAllExchangeRates();
        Set<ExchangeRateDTO> exchangeRateDTOS = new HashSet<>();

        for (ExchangeRate exchangeRate : exchangeRates) {
            exchangeRateDTOS.add(convertToDTO(exchangeRate));
        }
        return exchangeRateDTOS;
    }

    private Currency convertToCurrency(CurrencyCreationDTO currencyCreationDTO) {
        return new Currency(null, currencyCreationDTO.getCode(), currencyCreationDTO.getName(), currencyCreationDTO.getSign());
    }

    private CurrencyDTO convertToDTO(Currency currency) {
        return new CurrencyDTO(currency.getId(), currency.getCode(), currency.getName(), currency.getSign());
    }

    private ExchangeRateDTO convertToDTO(ExchangeRate exchangeRate) {
        return new ExchangeRateDTO(
                convertToDTO(exchangeRate.getBaseCurrency()),
                convertToDTO(exchangeRate.getTargetCurrency()),
                exchangeRate.getRate());
    }
}
