package org.kivislime.currencyexchange.model;

import org.kivislime.currencyexchange.DatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDaoImpl implements CurrencyDao {
    @Override
    public List<Currency> getCurrencies() {
        String sql = "select * from currencies";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Currency> currencies = new ArrayList<>();
            while (rs.next()) {
                Long id = rs.getLong("id");
                String code = rs.getString("code");
                String name = rs.getString("fullName");
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
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, currency.getCode());
            stmt.setString(2, currency.getFullName());
            stmt.setString(3, currency.getSign());
            int rs = stmt.executeUpdate();
            if (rs == 0) {
                throw new SQLException("Failed to insert currency");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currency;
    }
}
