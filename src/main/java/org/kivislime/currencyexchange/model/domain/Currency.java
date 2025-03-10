package org.kivislime.currencyexchange.model.domain;

import java.util.Objects;

public class Currency {
    private final Long id;
    private final String code;
    private final String name;
    private final String sign;

    public Currency(Long id, String code, String name, String sign) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.sign = sign;
    }

    public Long getId() {
        return id;
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
        return Objects.hash(id, code, name, sign);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Currency that = (Currency) obj;
        return Objects.equals(id, that.id) &&
                Objects.equals(code, that.code) &&
                Objects.equals(name, that.name) &&
                Objects.equals(sign, that.sign);
    }
}

