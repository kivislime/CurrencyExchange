package org.kivislime.currencyexchange.util;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.kivislime.currencyexchange.exception.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionHandlingFilter implements Filter {

    private static final Logger logger = Logger.getLogger(ExceptionHandlingFilter.class.getName());

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            logger.log(Level.WARNING, "Resource not found: {0}", e.getMessage());
            writeJsonError((HttpServletResponse) servletResponse, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (CurrencyAlreadyExistsException | ExchangeRateAlreadyExistsException e) {
            logger.log(Level.WARNING, "Resource conflict: {0}", e.getMessage());
            writeJsonError((HttpServletResponse) servletResponse, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (DaoException e) {
            logger.log(Level.SEVERE, "Database error: " + e.getMessage(), e);
            writeJsonError((HttpServletResponse) servletResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error: " + e.getMessage(), e);
            writeJsonError((HttpServletResponse) servletResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    private void writeJsonError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            out.write("{\"message\": \"" + message + "\"}");
            out.flush();
        }
    }
}
