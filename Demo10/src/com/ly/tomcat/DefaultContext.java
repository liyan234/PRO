package com.ly.tomcat;

import com.ly.standard.Servlet;

public class DefaultContext extends Context{
    public DefaultContext(ConfigReader reader) {
        super(reader,"/");
    }

    @Override
    public Servlet getServlet(String servletPath) {
        return HttpServer.notFoundServlet;
    }
}
