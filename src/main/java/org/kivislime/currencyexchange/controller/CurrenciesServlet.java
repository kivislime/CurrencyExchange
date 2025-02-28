package org.kivislime.currencyexchange.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.kivislime.currencyexchange.model.CurrencyCreationDTO;
import org.kivislime.currencyexchange.model.CurrencyDTO;
import org.kivislime.currencyexchange.util.JsonUtil;
import org.kivislime.currencyexchange.service.CurrencyService;

import java.io.IOException;
import java.util.Set;

@WebServlet(value = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyService currencyService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.currencyService = (CurrencyService) context.getAttribute("currencyService");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Set<CurrencyDTO> currencyDTOS = currencyService.getAllCurrencies();
        String json = JsonUtil.toJson(currencyDTOS);
        response.getWriter().write(json);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        String name = request.getParameter("name");
        String sign = request.getParameter("sign");

        if (code == null || name == null || sign == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Missing required parameters\"}");
            return;
        }

        CurrencyCreationDTO currencyCreationDTO = new CurrencyCreationDTO(code, name, sign);

        CurrencyDTO result = currencyService.addCurrency(currencyCreationDTO);
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write(JsonUtil.toJson(result));
    }

}
