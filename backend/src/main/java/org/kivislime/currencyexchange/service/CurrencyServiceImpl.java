package org.kivislime.currencyexchange.service;

import org.kivislime.currencyexchange.exception.CurrencyAlreadyExistsException;
import org.kivislime.currencyexchange.exception.CurrencyNotFoundException;
import org.kivislime.currencyexchange.exception.ExchangeRateAlreadyExistsException;
import org.kivislime.currencyexchange.exception.ExchangeRateNotFoundException;
import org.kivislime.currencyexchange.model.dao.CurrencyDao;
import org.kivislime.currencyexchange.model.domain.Currency;
import org.kivislime.currencyexchange.model.domain.ExchangeRate;
import org.kivislime.currencyexchange.model.domain.ExchangeResult;
import org.kivislime.currencyexchange.model.dto.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public boolean currencyExists(String currencyCode) {
        return currencyDao.currencyExists(currencyCode);
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
            throw new ExchangeRateNotFoundException("Exchange rate for " + baseCurrency.getCode() + " to " + targetCurrency.getCode() + " not found");
        }

        return convertToDTO(optionalExchangeRate.get());
    }

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
            throw new ExchangeRateAlreadyExistsException("Exchange Rate " +
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

    @Override
    public ExchangeResultDTO exchange(ExchangeResultCreationDTO exchangeResultCreationDTO) {
        Currency baseCurrency = currencyDao.getCurrency(exchangeResultCreationDTO.getFrom())
                .orElseThrow(() -> new CurrencyNotFoundException("Currency " + exchangeResultCreationDTO.getFrom() + " not found"));
        Currency targetCurrency = currencyDao.getCurrency(exchangeResultCreationDTO.getTo())
                .orElseThrow(() -> new CurrencyNotFoundException("Currency " + exchangeResultCreationDTO.getTo() + " not found"));
        BigDecimal amount = new BigDecimal(exchangeResultCreationDTO.getAmount());


        Optional<ExchangeResult> exchangeResultDirectExchange = calculateDirectExchange(baseCurrency, targetCurrency, amount);
        if (exchangeResultDirectExchange.isPresent()) {
            return convertToDTO(exchangeResultDirectExchange.get());
        }

        Optional<ExchangeResult> exchangeResultIndirectExchange = calculateIndirectExchange(baseCurrency, targetCurrency, amount);
        if (exchangeResultIndirectExchange.isPresent()) {
            return convertToDTO(exchangeResultIndirectExchange.get());
        } else {
            throw new ExchangeRateNotFoundException("Exchange rate for "
                    + baseCurrency.getCode() + " to " + targetCurrency.getCode() + " not found");
        }
    }

    private Optional<ExchangeResult> calculateDirectExchange(Currency baseCurrency, Currency
            targetCurrency, BigDecimal amount) {

        Optional<BigDecimal> optionalRate = currencyDao.getRate(baseCurrency.getId(), targetCurrency.getId());
        Optional<BigDecimal> optionalInverseRate = currencyDao.getRate(targetCurrency.getId(), baseCurrency.getId());
        boolean needsInversion = false;

        if (optionalRate.isEmpty()) {
            if (optionalInverseRate.isEmpty()) {
                return Optional.empty();
            } else {
                needsInversion = true;
                optionalRate = optionalInverseRate;
            }
        }

        BigDecimal normalizedRate = needsInversion ?
                BigDecimal.ONE.divide(optionalRate.get(), 10, RoundingMode.HALF_EVEN) : optionalRate.get();

        BigDecimal convertedAmount = amount.multiply(normalizedRate);
        ExchangeResult exchangeResult = new ExchangeResult(
                baseCurrency,
                targetCurrency,
                normalizedRate,
                amount,
                convertedAmount
        );

        return Optional.of(exchangeResult);
    }

    private Optional<ExchangeResult> calculateIndirectExchange(Currency baseCurrency, Currency
            targetCurrency, BigDecimal amount) {
        Set<Long> intersectIds = currencyDao.getIntersectIds(baseCurrency, targetCurrency);

        for (Long candidateCurrencyId : intersectIds) {
            Optional<BigDecimal> optionalBaseCurrencyRate = currencyDao.getRate(baseCurrency.getId(), candidateCurrencyId);
            Optional<BigDecimal> optionalTargetCurrencyRate = currencyDao.getRate(targetCurrency.getId(), candidateCurrencyId);

            BigDecimal baseCurrencyRate = optionalBaseCurrencyRate.orElseThrow(
                    () -> new CurrencyNotFoundException("Rate for " + baseCurrency.getCode() + " not found"));
            BigDecimal targetCurrencyRate = optionalTargetCurrencyRate.orElseThrow(
                    () -> new CurrencyNotFoundException("Rate for " + targetCurrency.getCode() + " not found")
            );

            BigDecimal calculatedRate = baseCurrencyRate.divide(targetCurrencyRate, 10, RoundingMode.HALF_EVEN);
            BigDecimal convertedAmount = amount.multiply(calculatedRate);

            ExchangeResult exchangeResult = new ExchangeResult(
                    baseCurrency,
                    targetCurrency,
                    calculatedRate,
                    amount,
                    convertedAmount);

            return Optional.of(exchangeResult);
        }
        return Optional.empty();
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

    private ExchangeResultDTO convertToDTO(ExchangeResult exchangeResult) {
        return new ExchangeResultDTO(exchangeResult.getBaseCurrency(), exchangeResult.getTargetCurrency(),
                exchangeResult.getRate(), exchangeResult.getAmount(), exchangeResult.getConvertedAmount());
    }
}
