package org.kivislime.currencyexchange.exception;

public class ExchangeRateNotFoundException extends ServiceException {
    public ExchangeRateNotFoundException(String message) {
        super(message);
    }
}
