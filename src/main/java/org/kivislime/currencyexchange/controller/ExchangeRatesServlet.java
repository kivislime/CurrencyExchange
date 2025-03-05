package org.kivislime.currencyexchange.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.kivislime.currencyexchange.model.ExchangeRate;
import org.kivislime.currencyexchange.model.ExchangeRateCreationDTO;
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        if (baseCurrencyCode == null || targetCurrencyCode == null || rate == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Missing required parameters\"}");
            return;
        }

        ExchangeRateCreationDTO exchangeRateCreationDTO = new ExchangeRateCreationDTO(baseCurrencyCode, targetCurrencyCode, rate);
        ExchangeRateDTO result = currencyService.addExchangeRate(exchangeRateCreationDTO);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(JsonUtil.toJson(result));

    }

}
