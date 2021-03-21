package com.ly.standard.http;

import com.ly.standard.ServletResponse;

public interface HttpServletResponse extends ServletResponse {

    void addCookie(Cookie cookie);

    void sendError(int sc);

    void sendRedirect(String location);

    void setHeader(String name, String value);

    void setStatus(int sc);
}
