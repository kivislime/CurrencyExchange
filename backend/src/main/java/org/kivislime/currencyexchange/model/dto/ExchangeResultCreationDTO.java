package org.kivislime.currencyexchange.model.dto;


import java.util.Objects;

public class ExchangeResultCreationDTO {
    private final String from;
    private final String to;
    private final String amount;

    public ExchangeResultCreationDTO(String from, String to, String amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeResultCreationDTO that = (ExchangeResultCreationDTO) o;
        return Objects.equals(from, that.from) && Objects.equals(to, that.to) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, amount);
    }
}