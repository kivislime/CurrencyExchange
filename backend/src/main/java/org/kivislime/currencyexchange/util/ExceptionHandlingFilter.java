package org.kivislime.currencyexchange.util;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.kivislime.currencyexchange.exception.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionHandlingFilter implements Filter {

    private static final Logger logger = Logger.getLogger(ExceptionHandlingFilter.class.getName());

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            logger.log(Level.WARNING, "Resource not found: {0}", e.getMessage());
            ((HttpServletResponse) servletResponse)
                    .sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");

        } catch (CurrencyAlreadyExistsException | ExchangeRateAlreadyExistsException e) {
            logger.log(Level.WARNING, "Resource conflict: {0}", e.getMessage());
            ((HttpServletResponse) servletResponse)
                    .sendError(HttpServletResponse.SC_CONFLICT, "Resource already exists");

        } catch (DaoException e) {
            logger.log(Level.SEVERE, "Database error: " + e.getMessage(), e);
            ((HttpServletResponse) servletResponse)
                    .sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error: " + e.getMessage(), e);
            ((HttpServletResponse) servletResponse)
                    .sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");

        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
