package com.ly.standard.http;

public interface HttpSession {

    /** 获取session*/
    Object getAttribute(String name);

    /** 构建session*/
    void removeAttribute(String name);

    /** 设置session*/
    void setAttribute(String name, Object value);

}
