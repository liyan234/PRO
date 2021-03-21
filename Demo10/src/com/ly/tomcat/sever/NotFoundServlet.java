package com.ly.tomcat.sever;

import com.ly.standard.ServletException;
import com.ly.standard.http.HttpServlet;
import com.ly.standard.http.HttpServletRequest;
import com.ly.standard.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class NotFoundServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.setStatus(404);
        resp.setContentType("text/html");
        PrintWriter printWriter = resp.getWriter();
        printWriter.println("<h1>该资源不存在<h1>");
    }
}
