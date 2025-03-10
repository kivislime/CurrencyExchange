package org.kivislime.currencyexchange.model.dto;

import java.util.Objects;

public class CurrencyCreationDTO {
    private final String code;
    private final String name;
    private final String sign;

    public CurrencyCreationDTO(String code, String name, String sign) {
        this.code = code;
        this.name = name;
        this.sign = sign;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, sign);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        CurrencyCreationDTO that = (CurrencyCreationDTO) obj;
        return Objects.equals(code, that.code) &&
                Objects.equals(name, that.name) &&
                Objects.equals(sign, that.sign);
    }
}
