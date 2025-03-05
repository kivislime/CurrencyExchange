package org.kivislime.currencyexchange.model;

import org.kivislime.currencyexchange.DatabaseConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

//TODO: нет обработки, если таблица пустая. Че выдаст?
//TODO: переименоват домены бд? Всё с маленькой буквы
public class CurrencyDaoImpl implements CurrencyDao {
    @Override
    public Set<Currency> getCurrencies() {
        String sql = "SELECT * FROM currencies";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            Set<Currency> currencies = new HashSet<>();
            while (rs.next()) {
                Long id = rs.getLong("id");
                String code = rs.getString("code");
                String name = rs.getString("fullname");
                String sign = rs.getString("sign");
                Currency currency = new Currency(id, code, name, sign);
                currencies.add(currency);
            }
            return currencies;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean currencyExists(String currency) {
        String sql = "SELECT code FROM currencies WHERE code = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, currency);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Currency> getCurrency(String currencyCode) {
        String sql = "SELECT id, code, fullname, sign FROM currencies WHERE code = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, currencyCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Currency currency = new Currency(
                            rs.getLong("id"),
                            rs.getString("code"),
                            rs.getString("fullname"),
                            rs.getString("sign")
                    );
                    return Optional.of(currency);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Currency addCurrency(Currency currency) {
        String sql = "INSERT INTO currencies(code, fullname, sign) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, currency.getCode());
            stmt.setString(2, currency.getName());
            stmt.setString(3, currency.getSign());
            int rs = stmt.executeUpdate();
            if (rs == 0) {
                throw new SQLException("Failed to insert currency");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Currency(generatedKeys.getLong(1), currency.getCode(), currency.getName(), currency.getSign());
                } else {
                    throw new SQLException("Inserting currency failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<ExchangeRate> getAllExchangeRates() {
        String sql = "SELECT er.id       AS exchange_id,\n" +
                "       c.id        AS base_id,\n" +
                "       c.code      AS base_code,\n" +
                "       c.fullname  AS base_name,\n" +
                "       c.sign      AS base_sign,\n" +
                "       c2.id       AS target_id,\n" +
                "       c2.code     AS target_code,\n" +
                "       c2.fullname AS target_name,\n" +
                "       c2.sign     AS target_sign,\n" +
                "       er.rate\n" +
                "FROM exchangerates er\n" +
                "         JOIN public.currencies c on c.id = er.basecurrencyid\n" +
                "         JOIN public.currencies c2 on c2.id = er.targetcurrencyid\n";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            Set<ExchangeRate> exchangeRates = new HashSet<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long exchangeId = rs.getLong("exchange_id");
                    BigDecimal rate = rs.getBigDecimal("rate"); //TODO: исправить на bigdecimal!

                    Currency baseCurrency = new Currency(
                            rs.getLong("base_id"),
                            rs.getString("base_code"),
                            rs.getString("base_name"),
                            rs.getString("base_sign"));

                    Currency targetCurrency = new Currency(
                            rs.getLong("target_id"),
                            rs.getString("target_code"),
                            rs.getString("target_name"),
                            rs.getString("target_sign"));

                    exchangeRates.add(new ExchangeRate(exchangeId, baseCurrency, targetCurrency, rate));
                }
                return exchangeRates;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExchangeRate> getExchangeRateByPair(Currency baseCurrency, Currency targetCurrency) {
        String sql = "SELECT * FROM exchangerates WHERE basecurrencyid = ? AND targetcurrencyid = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, baseCurrency.getId());
            stmt.setLong(2, targetCurrency.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new ExchangeRate(
                            rs.getLong("id"),
                            baseCurrency,
                            targetCurrency,
                            rs.getBigDecimal("rate")
                    ));
                } else
                    return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exchangeRateExists(Long baseId, Long targetId) {
        String sql = "SELECT * FROM exchangerates WHERE basecurrencyid = ? AND targetcurrencyid = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, baseId);
            stmt.setLong(2, targetId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExchangeRate addExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        String sql = "INSERT INTO exchangerates(basecurrencyid, targetcurrencyid, rate) VALUES(?, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, baseCurrency.getId());
            stmt.setLong(2, targetCurrency.getId());
            stmt.setBigDecimal(3, rate);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to insert exchange rate");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return new ExchangeRate(rs.getLong(1), baseCurrency, targetCurrency, rate);
                } else {
                    throw new SQLException("Inserting currency exchange rate, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExchangeRate patchExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        String sql = "UPDATE exchangerates SET rate = ? WHERE basecurrencyid = ? AND targetcurrencyid = ? RETURNING id, rate";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setBigDecimal(1, rate);
            stmt.setLong(2, baseCurrency.getId());
            stmt.setLong(3, targetCurrency.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ExchangeRate(
                            rs.getLong("id"),
                            baseCurrency,
                            targetCurrency,
                            rs.getBigDecimal("rate")
                    );
                } else {
                    throw new SQLException("Updating exchange rate failed, no row returned.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
