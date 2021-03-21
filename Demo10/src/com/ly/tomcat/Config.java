package com.ly.tomcat;

import java.util.LinkedHashMap;
import java.util.Map;

public class Config {
    // 配置文件的就是两种
    /*# ServletName = ServletClassName
          TranslateServlet = com.ly.webapps.dictionary.TranslateServlet

        servlet-mappings:
          # URLPattern = ServletName
          /translate = TranslateServlet
         */
    Map<String, String> servletNameToServletClassNameMap;
    LinkedHashMap<String, String> urlPatternToServletNameMap;

    public Config(Map<String, String> servletNameToServletClassNameMap, LinkedHashMap<String, String> urlPatternToServletNameMap) {
        this.servletNameToServletClassNameMap = servletNameToServletClassNameMap;
        this.urlPatternToServletNameMap = urlPatternToServletNameMap;
    }
}
