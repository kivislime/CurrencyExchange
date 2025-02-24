package org.kivislime.currencyexchange.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.kivislime.currencyexchange.model.CurrencyDTO;
import org.kivislime.currencyexchange.util.JsonUtil;
import org.kivislime.currencyexchange.model.CurrencyDaoImpl;
import org.kivislime.currencyexchange.service.CurrencyService;
import org.kivislime.currencyexchange.service.CurrencyServiceImpl;

import java.io.IOException;
import java.util.List;

@WebServlet(value = "/currencies")
public class GetCurrenciesServlet extends HttpServlet {
    private CurrencyService currencyService;

    @Override
    public void init() {
        this.currencyService = new CurrencyServiceImpl(new CurrencyDaoImpl());
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        List<CurrencyDTO> currencyDTOS = currencyService.getAllCurrencies();
        String json = JsonUtil.toJson(currencyDTOS);
        response.getWriter().write(json);
    }
}
