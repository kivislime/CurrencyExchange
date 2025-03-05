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
import org.kivislime.currencyexchange.util.ParseUtil;

import java.io.IOException;
import java.util.Map;

@WebServlet(value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private CurrencyService currencyService;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        currencyService = (CurrencyService) servletContext.getAttribute("currencyService");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if ("PATCH".equalsIgnoreCase(method)) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
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

        if (!pathInfo.matches("^[A-Z]{6}$")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid currencies code. Expected 6 uppercase letters.\"}");
            return;
        }

        ExchangeRateDTO exchangeRateDTO = currencyService.getExchangeRateByPair(pathInfo);
        resp.setStatus(HttpServletResponse.SC_OK);//TODO: расставить везде явно статусы
        resp.getWriter().write(JsonUtil.toJson(exchangeRateDTO));
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String body = ParseUtil.requestBodyToString(req);
        Map<String, String> parsedParams = ParseUtil.parseBody(body);

        String pathInfo = req.getPathInfo();
        String rate = parsedParams.get("rate");

        if (rate == null || rate.isEmpty() || pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Rate is required\"}");
            return;
        }

        ExchangeRateDTO exchangeRateDTO = currencyService.patchExchangeRate(pathInfo, rate);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(JsonUtil.toJson(exchangeRateDTO));
    }

}
