package com.keessi.shiro.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import org.apache.shiro.guice.web.ShiroWebModule;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

public class ShiroGuiceBootstrap extends GuiceServletContextListener {
    private ServletContext servletContext;

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        servletContext = servletContextEvent.getServletContext();
        super.contextInitialized(servletContextEvent);
    }

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ShiroServletModule(servletContext), ShiroWebModule.guiceFilterModule());
    }
}
