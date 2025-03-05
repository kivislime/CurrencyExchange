package org.kivislime.currencyexchange.service;

import org.kivislime.currencyexchange.model.*;

import java.math.BigDecimal;
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
        //TODO: добавить проверку перед вставкой, так как нужно возвращать код ошибки о неудаче
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
        if (pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1);
        }

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

    //TODO: изменить ошибки их типы
    @Override
    public ExchangeRateDTO addExchangeRate(ExchangeRateCreationDTO exchangeRateCreationDTO) {
        Currency baseCurrency = currencyDao.getCurrency(exchangeRateCreationDTO.getBaseCurrency())
                .orElseThrow(() -> new CurrencyNotFoundException("Currency " + exchangeRateCreationDTO.getBaseCurrency() + " not found"));
        Currency targetCurrency = currencyDao.getCurrency(exchangeRateCreationDTO.getTargetCurrency()).
                orElseThrow(() -> new CurrencyNotFoundException("Currency " + exchangeRateCreationDTO.getTargetCurrency() + " not found"));

        if (!currencyDao.exchangeRateExists(baseCurrency.getId(), targetCurrency.getId())) {
            BigDecimal rate = new BigDecimal(exchangeRateCreationDTO.getRate());
            ExchangeRate exchangeRate = currencyDao.addExchangeRate(baseCurrency, targetCurrency, rate);

            return convertToDTO(exchangeRate);
        } else {
            throw new CurrencyNotFoundException("Exchange Rate " +
                    exchangeRateCreationDTO.getBaseCurrency() +
                    exchangeRateCreationDTO.getTargetCurrency() +
                    " already exists");
        }
    }

    @Override
    public ExchangeRateDTO patchExchangeRate(String pathInfo, String rate) {
        if (pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1);
        }

        String firstOfPair = pathInfo.substring(0, pathInfo.length() / 2);
        String secondOfPair = pathInfo.substring(pathInfo.length() / 2);
        BigDecimal rateInDecimal = new BigDecimal(rate);

        Currency baseCurrency = currencyDao.getCurrency(firstOfPair)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency " + firstOfPair + " not found"));
        Currency targetCurrency = currencyDao.getCurrency(secondOfPair)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency " + secondOfPair + " not found"));

        ExchangeRate exchangeRate = currencyDao.patchExchangeRate(baseCurrency, targetCurrency, rateInDecimal);

        return convertToDTO(exchangeRate);
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
