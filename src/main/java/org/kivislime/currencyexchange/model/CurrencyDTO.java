package org.kivislime.currencyexchange.model;

import java.util.Objects;

public class CurrencyDTO {
    private final Long id;
    private final String code;
    private final String fullName;
    private final String sign;

    public CurrencyDTO(Long id, String code, String fullName, String sign) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public long getId() {
        return id;
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
        return Objects.hash(id, code, fullName, sign);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        CurrencyDTO that = (CurrencyDTO) obj;
        return Objects.equals(id, that.id) &&
                Objects.equals(code, that.code) &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(sign, that.sign);
    }
}
