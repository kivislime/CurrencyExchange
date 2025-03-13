package org.kivislime.currencyexchange.model.dao;

import org.kivislime.currencyexchange.DatabaseConnectionManager;
import org.kivislime.currencyexchange.exception.CurrencyAlreadyExistsException;
import org.kivislime.currencyexchange.exception.DaoException;
import org.kivislime.currencyexchange.model.domain.Currency;
import org.kivislime.currencyexchange.model.domain.ExchangeRate;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
                String name = rs.getString("full_name");
                String sign = rs.getString("sign");
                Currency currency = new Currency(id, code, name, sign);
                currencies.add(currency);
            }
            return currencies;
        } catch (SQLException e) {
            throw new DaoException("Error retrieving the list of currencies", e);
        }
    }

    @Override
    public boolean currencyExists(String currencyCode) {
        String sql = "SELECT code FROM currencies WHERE code = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, currencyCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DaoException("Error retrieving currency with code: " + currencyCode, e);
        }
    }


    @Override
    public Optional<Currency> getCurrency(String currencyCode) {
        String sql = "SELECT id, code, full_name, sign FROM currencies WHERE code = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, currencyCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Currency currency = new Currency(
                            rs.getLong("id"),
                            rs.getString("code"),
                            rs.getString("full_name"),
                            rs.getString("sign")
                    );
                    return Optional.of(currency);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error retrieving currency with code: " + currencyCode, e);
        }
    }

    @Override
    public Currency addCurrency(Currency currency) {
        String sql = "INSERT INTO currencies(code, full_name, sign) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, currency.getCode());
            stmt.setString(2, currency.getName());
            stmt.setString(3, currency.getSign());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Currency(generatedKeys.getLong(1), currency.getCode(), currency.getName(), currency.getSign());
                } else {
                    throw new DaoException("Error inserting currency, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {  // SQL State 23505 = Unique Violation
                throw new CurrencyAlreadyExistsException("Currency already exists: " + currency.getCode());
            }
            throw new DaoException("Error inserting currency: " + currency.getCode(), e);
        }
    }

    @Override
    public Set<ExchangeRate> getAllExchangeRates() {
        String sql = "SELECT er.id       AS exchange_id,\n" +
                "       c.id        AS base_id,\n" +
                "       c.code      AS base_code,\n" +
                "       c.full_name  AS base_name,\n" +
                "       c.sign      AS base_sign,\n" +
                "       c2.id       AS target_id,\n" +
                "       c2.code     AS target_code,\n" +
                "       c2.full_name AS target_name,\n" +
                "       c2.sign     AS target_sign,\n" +
                "       er.rate\n" +
                "FROM exchange_rates er\n" +
                "         JOIN public.currencies c on c.id = er.base_currency_id\n" +
                "         JOIN public.currencies c2 on c2.id = er.target_currency_id\n";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            Set<ExchangeRate> exchangeRates = new HashSet<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long exchangeId = rs.getLong("exchange_id");
                    BigDecimal rate = rs.getBigDecimal("rate");

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
            throw new DaoException("Error retrieving the list of exchange rates", e);
        }
    }

    @Override
    public Optional<ExchangeRate> getExchangeRateByPair(Currency baseCurrency, Currency targetCurrency) {
        String sql = "SELECT * FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?";
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
            throw new DaoException("Error retrieving currency rate", e);
        }
    }

    @Override
    public boolean exchangeRateExists(Long baseId, Long targetId) {
        String sql = "SELECT * FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, baseId);
            stmt.setLong(2, targetId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DaoException("Error retrieving currency rate", e);
        }
    }

    @Override
    public ExchangeRate addExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        String sql = "INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate) VALUES(?, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, baseCurrency.getId());
            stmt.setLong(2, targetCurrency.getId());
            stmt.setBigDecimal(3, rate);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Error inserting exchange rate, no row returned.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return new ExchangeRate(rs.getLong(1), baseCurrency, targetCurrency, rate);
                } else {
                    throw new DaoException("Error inserting currency exchange rate, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new DaoException("Error inserting currency exchange rate", e);
        }
    }

    @Override
    public ExchangeRate patchExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        String sql = "UPDATE exchange_rates SET rate = ? WHERE base_currency_id = ? AND target_currency_id = ? RETURNING id, rate";
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
                    throw new DaoException("Error updating exchange rate, no row returned.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error updating exchange rate", e);
        }
    }

    public Set<Long> getExchangeableCurrencyIdsForCurrency(Long id) {
        String sql = "SELECT target_currency_id FROM exchange_rates WHERE base_currency_id = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);

            Set<Long> result = new HashSet<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getLong("target_currency_id"));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DaoException("Error retrieving currency", e);
        }
    }

    @Override
    public Optional<BigDecimal> getRate(Long base_currency_id, Long target_currency_id) {
        String sql = "SELECT rate FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, base_currency_id);
            stmt.setLong(2, target_currency_id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getBigDecimal("rate"));
                } else {
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            throw new DaoException("Error retrieving rate", e);
        }
    }

    @Override
    public Set<Long> getIntersectIds(Currency firstCurrency, Currency secondCurrency) {
        String sql = "SELECT target_currency_id\n" +
                "FROM exchange_rates\n" +
                "WHERE base_currency_id = ?\n" +
                "INTERSECT\n" +
                "SELECT target_currency_id\n" +
                "FROM exchange_rates\n" +
                "WHERE base_currency_id = ?;\n";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, firstCurrency.getId());
            stmt.setLong(2, secondCurrency.getId());

            Set<Long> result = new HashSet<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getLong("target_currency_id"));
                }
            }

            return result;

        } catch (SQLException e) {
            throw new DaoException("Error retrieving ids", e);
        }
    }
}
