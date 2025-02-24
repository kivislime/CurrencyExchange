package org.kivislime.currencyexchange.model;

import java.util.Objects;

public class CurrencyDTO {
    private final String code;
    private final String fullName;
    private final String sign;

    public CurrencyDTO(String code, String fullName, String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public String getCode() {
        return code;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, fullName, sign);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        CurrencyDTO that = (CurrencyDTO) obj;
        return Objects.equals(code, that.code) &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(sign, that.sign);
    }
}
