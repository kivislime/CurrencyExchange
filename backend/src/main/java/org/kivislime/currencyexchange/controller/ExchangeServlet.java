package org.kivislime.currencyexchange.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.kivislime.currencyexchange.model.dto.ExchangeResultCreationDTO;
import org.kivislime.currencyexchange.model.dto.ExchangeResultDTO;
import org.kivislime.currencyexchange.service.CurrencyService;
import org.kivislime.currencyexchange.util.JsonUtil;

import java.io.IOException;

@WebServlet(value = "/exchange")
public class ExchangeServlet extends HttpServlet {
    private CurrencyService currencyService;

    @Override
    public void init() throws ServletException {
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
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        ExchangeResultCreationDTO exchangeResultCreationDTO = new ExchangeResultCreationDTO(from, to, amount);
        ExchangeResultDTO convertedExchange = currencyService.exchange(exchangeResultCreationDTO);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(JsonUtil.toJson(convertedExchange));
    }

}

