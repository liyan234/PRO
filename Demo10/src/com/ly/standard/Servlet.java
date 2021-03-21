package com.ly.standard;

import java.io.IOException;

public interface Servlet {

    //初始化方法
    void init() throws ServletException;

    //一般的执行方法
    void service (ServletRequest req, ServletResponse resp) throws ServletException, IOException;

    //销毁
    void destroy();

}
