package org.kivislime.currencyexchange.exception;

public class CurrencyAlreadyExistsException extends ServiceException {
    public CurrencyAlreadyExistsException(String message) {
        super(message);
    }
}
