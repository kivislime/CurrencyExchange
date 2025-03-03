package org.kivislime.currencyexchange.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.kivislime.currencyexchange.model.ExchangeRateDTO;
import org.kivislime.currencyexchange.service.CurrencyService;
import org.kivislime.currencyexchange.util.JsonUtil;

import java.io.IOException;

@WebServlet(value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private CurrencyService currencyService;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        currencyService = (CurrencyService) servletContext.getAttribute("currencyService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Rates is required\"}");
            return;
        }
        pathInfo = pathInfo.substring(1);

        if (!pathInfo.matches("^[A-Z]{6}$")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid currencies code. Expected 6 uppercase letters.\"}");
            return;
        }

        ExchangeRateDTO exchangeRateDTO = currencyService.getExchangeRateByPair(pathInfo);
        resp.setStatus(HttpServletResponse.SC_OK);//TODO: расставить везде явно статусы
        resp.getWriter().write(JsonUtil.toJson(exchangeRateDTO));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

//        String pathInfo = req.getPathInfo();
//        if (pathInfo == null || pathInfo.equals("/")) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            resp.getWriter().write("{\"error\":\"Rates is required\"}");
//        }
//        pathInfo = pathInfo.substring(1);
//        if (!pathInfo.matches("^[A-Z]{6}$")) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        }
//        ExchangeRateDTO exchangeRateDTO = currencyService.getExchangeRateByPair(pathInfo);
//        resp.setStatus(HttpServletResponse.SC_OK);
//        resp.getWriter().write(JsonUtil.toJson(exchangeRateDTO));

    }
}
