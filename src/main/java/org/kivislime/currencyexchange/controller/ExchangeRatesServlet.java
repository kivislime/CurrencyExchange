package org.kivislime.currencyexchange.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.kivislime.currencyexchange.model.ExchangeRate;
import org.kivislime.currencyexchange.model.ExchangeRateDTO;
import org.kivislime.currencyexchange.service.CurrencyService;
import org.kivislime.currencyexchange.util.JsonUtil;

import java.io.IOException;
import java.util.Set;

@WebServlet(value = "/exchangeRates/*")
public class ExchangeRatesServlet extends HttpServlet {
    private CurrencyService currencyService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.currencyService = (CurrencyService) context.getAttribute("currencyService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        Set<ExchangeRateDTO> exchangeRateDTOS = currencyService.getAllExchangeRates();
        String json = JsonUtil.toJson(exchangeRateDTOS);
        resp.getWriter().write(json);
    }
}
