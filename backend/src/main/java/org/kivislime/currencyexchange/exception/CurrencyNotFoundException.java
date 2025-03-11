package org.kivislime.currencyexchange.exception;

public class CurrencyNotFoundException extends ServiceException {
    private static final long serialVersionUID = 1L;

    public CurrencyNotFoundException(String message) {
        super(message);
    }

    public CurrencyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

