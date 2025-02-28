package org.kivislime.currencyexchange.service;

public class CurrencyNotFoundException extends RuntimeException  {
    public CurrencyNotFoundException(String message) {
        super(message);
    }
}
