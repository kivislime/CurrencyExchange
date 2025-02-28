package org.kivislime.currencyexchange.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.kivislime.currencyexchange.model.CurrencyDaoImpl;
import org.kivislime.currencyexchange.service.CurrencyService;
import org.kivislime.currencyexchange.service.CurrencyServiceImpl;

//TODO: добавить логирование
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
        // Метод вызывается при остановке приложения
        // Можно освободить ресурсы, закрыть соединения и т.п.
        System.out.println("Application context destroyed.");
    }
}
