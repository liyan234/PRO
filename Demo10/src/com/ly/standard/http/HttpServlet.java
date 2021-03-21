package com.ly.standard.http;

import com.ly.standard.Servlet;
import com.ly.standard.ServletException;
import com.ly.standard.ServletRequest;
import com.ly.standard.ServletResponse;

import java.io.IOException;

public abstract class HttpServlet implements Servlet {
    @Override
    public void init() throws ServletException {

    }

    @Override
    public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
        // instanceof运算符的前一个操作符是一个引用变量，
        // 后一个操作数通常是一个类（可以是接口），
        // 用于判断前面的对象是否是后面的类，或者其子类、实现类的实例
        if(req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
            HttpServletRequest httpReq = (HttpServletRequest) req;
            HttpServletResponse httpResp = (HttpServletResponse) resp;
            service(httpReq, httpResp);
        } else  {
            throw new ServletException("不支持非Http协议");
        }
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (req.getMethod().equals("GET")) {
            doGet(req, resp);
        } else  {
            resp.sendError(405);
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
        resp.sendError(405);
    }

    @Override
    public void destroy() {

    }
}
