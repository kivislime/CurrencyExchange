package org.kivislime.currencyexchange.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.kivislime.currencyexchange.model.dao.CurrencyDaoImpl;
import org.kivislime.currencyexchange.service.CurrencyService;
import org.kivislime.currencyexchange.service.CurrencyServiceImpl;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        CurrencyService currencyService = new CurrencyServiceImpl(new CurrencyDaoImpl());

        ServletContext context = sce.getServletContext();
        context.setAttribute("currencyService", currencyService);

        System.out.println("Application context initialized and dependencies are set.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Application context destroyed.");
    }
}
