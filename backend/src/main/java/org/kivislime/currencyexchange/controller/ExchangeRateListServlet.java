package org.kivislime.currencyexchange.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.kivislime.currencyexchange.model.dto.ExchangeRateCreationDTO;
import org.kivislime.currencyexchange.model.dto.ExchangeRateDTO;
import org.kivislime.currencyexchange.service.CurrencyService;
import org.kivislime.currencyexchange.util.JsonUtil;

import java.io.IOException;
import java.math.BigDecimal;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Set<ExchangeRateDTO> exchangeRateDTOS = currencyService.getAllExchangeRates();
        String json = JsonUtil.toJson(exchangeRateDTOS);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

        if(baseCurrencyCode.equals(targetCurrencyCode)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"message\":\"Currency code is same\"}");
            return;
        }

        BigDecimal rateInDecimal;
        try {
            String parsedRate = rate.replace(',', '.');
            rateInDecimal = new BigDecimal(parsedRate);
            if (rateInDecimal.compareTo(BigDecimal.ZERO) <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"message\":\"Rate must be greater than 0\"}");
                return;
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"message\":\"Invalid rate format\"}");
            return;
        }

        ExchangeRateCreationDTO exchangeRateCreationDTO = new ExchangeRateCreationDTO(baseCurrencyCode, targetCurrencyCode, rateInDecimal);
        ExchangeRateDTO result = currencyService.addExchangeRate(exchangeRateCreationDTO);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(JsonUtil.toJson(result));
    }

}
