package org.kivislime.currencyexchange.service;

import org.kivislime.currencyexchange.model.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyDao currencyDao;

    public CurrencyServiceImpl(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    @Override
    public Set<CurrencyDTO> getAllCurrencies() {
        return currencyDao.getCurrencies()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toSet());
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
        return currencyDao.getAllExchangeRates()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toSet());
    }

    @Override
    public ExchangeRateDTO getExchangeRateByPair(String pathInfo) {
        String firstOfPair = pathInfo.substring(0, pathInfo.length() / 2);
        String secondOfPair = pathInfo.substring(pathInfo.length() / 2);

        Currency baseCurrency = currencyDao.getCurrency(firstOfPair)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency " + firstOfPair + " not found"));
        Currency targetCurrency = currencyDao.getCurrency(secondOfPair)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency " + secondOfPair + " not found"));

        Optional<ExchangeRate> optionalExchangeRate = currencyDao.getExchangeRateByPair(baseCurrency, targetCurrency);

        if (optionalExchangeRate.isEmpty()) {
            //TODO: изменить на другой тип ошибки
            throw new CurrencyNotFoundException("Exchange rate for " + baseCurrency.getCode() + " to " + targetCurrency.getCode() + " not found");
        }

        return convertToDTO(optionalExchangeRate.get());
    }


    private Currency convertToCurrency(CurrencyCreationDTO currencyCreationDTO) {
        return new Currency(null, currencyCreationDTO.getCode(), currencyCreationDTO.getName(), currencyCreationDTO.getSign());
    }

    private CurrencyDTO convertToDTO(Currency currency) {
        return new CurrencyDTO(currency.getId(), currency.getCode(), currency.getName(), currency.getSign());
    }

    private ExchangeRateDTO convertToDTO(ExchangeRate exchangeRate) {
        return new ExchangeRateDTO(
                exchangeRate.getId(),
                convertToDTO(exchangeRate.getBaseCurrency()),
                convertToDTO(exchangeRate.getTargetCurrency()),
                exchangeRate.getRate());
    }
}
