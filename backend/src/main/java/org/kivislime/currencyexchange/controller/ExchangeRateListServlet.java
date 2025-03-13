package org.kivislime.currencyexchange.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.kivislime.currencyexchange.model.dto.ExchangeRateCreationDTO;
import org.kivislime.currencyexchange.model.dto.ExchangeRateDTO;
import org.kivislime.currencyexchange.service.CurrencyService;
import org.kivislime.currencyexchange.util.JsonUtil;

import java.io.IOException;
import java.util.Set;

@WebServlet(value = "/exchangeRates/*")
public class ExchangeRateListServlet extends HttpServlet {
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
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        if (baseCurrencyCode == null || targetCurrencyCode == null || rate == null ||
                baseCurrencyCode.trim().isEmpty() || targetCurrencyCode.trim().isEmpty() || rate.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"message\":\"Missing required parameters\"}");
            return;
        }

        try {
            String parsedRate = rate.replace(',', '.');
            ExchangeRateCreationDTO exchangeRateCreationDTO = new ExchangeRateCreationDTO(baseCurrencyCode, targetCurrencyCode, parsedRate);
            ExchangeRateDTO result = currencyService.addExchangeRate(exchangeRateCreationDTO);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(JsonUtil.toJson(result));

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"message\":\"Invalid rate format\"}");
        }

    }

}
