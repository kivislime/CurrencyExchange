package org.kivislime.currencyexchange.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.kivislime.currencyexchange.model.dto.ExchangeResultCreationDTO;
import org.kivislime.currencyexchange.model.dto.ExchangeResultDTO;
import org.kivislime.currencyexchange.service.CurrencyService;
import org.kivislime.currencyexchange.util.JsonUtil;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(value = "/exchange")
public class ExchangeServlet extends HttpServlet {
    private CurrencyService currencyService;

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        currencyService = (CurrencyService) servletContext.getAttribute("currencyService");
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amount = req.getParameter("amount");

        if (from == null || to == null || amount == null ||
                from.trim().isEmpty() || to.trim().isEmpty() || amount.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"message\":\"Missing or empty required parameters\"}");
            return;
        }

        BigDecimal amountInDecimal;
        try {
            amountInDecimal = new BigDecimal(amount);
            if (amountInDecimal.compareTo(BigDecimal.ZERO) < 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"message\":\"Amount must be greater or equal than zero\"}");
                return;
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"message\":\"Invalid amount format\"}");
            return;
        }

        ExchangeResultCreationDTO exchangeResultCreationDTO = new ExchangeResultCreationDTO(from, to, amountInDecimal);
        ExchangeResultDTO convertedExchange = currencyService.exchange(exchangeResultCreationDTO);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(JsonUtil.toJson(convertedExchange));
    }

}

