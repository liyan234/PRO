package com.ly.tomcat;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *  用来每一个context独立解析自己的配置文件
 *
 * */

// 配置文件格式
//            servlets:
//            # ServletName = ServletClassName
//            TranslateServlet = com.ly.webapps.dictionary.TranslateServlet
//
//            servlet-mappings:
//            # URLPattern = ServletName
//            /translate = TranslateServlet
public class ConfigReader {

    public Config read(String name) throws IOException {
        Map<String, String> servletNameToServletClassNameMap = new HashMap<>();
        LinkedHashMap<String, String> urlPatternToServletNameMap = new LinkedHashMap<>();
        // 获取到对应的配置文件的位置
        String fileName = String.format("%s/%s/WEB-INF\\web.conf", HttpServer.WEBAPPS_BASE, name);
        // 将解析的配置文件 其中url servletName servletClassName

        // 给stage一个初始值
        String stage = "start";
        try(InputStream inputStream = new FileInputStream(fileName)) {
            Scanner sc = new Scanner(inputStream, "UTF-8");
            // 每次读取一行
            while (sc.hasNextLine()) {
                String  line = sc.nextLine().trim();
                // 空行或者为注释行就跳过
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                switch (stage) {
                    case  "start" :
                        // 开始
                        if (line.equals("servlets:")) {
                            stage = "servlets";
                        }
                        break;
                    case "servlets" :
                        if (line.equals("servlet-mappings:")) {
                            stage = "mappings";
                        } else {
                            // 存入map中
                            // # ServletName = ServletClassName
                            String[] parts = line.split("=");
                            String servletName = parts[0].trim();
                            String servletClassName = parts[1].trim();
                            System.out.println(servletName +" "+ servletClassName);
                            servletNameToServletClassNameMap.put(servletName,  servletClassName);
                        }
                        break;
                    case "mappings" :
                        //# URLPattern = ServletName
                        String[] parts = line.split("=");
                        String uRLPattern = parts[0].trim();
                        String servletClassName = parts[1].trim();
                        System.out.println(uRLPattern + "=" +servletClassName);
                        urlPatternToServletNameMap.put(uRLPattern, servletClassName);
                        break;
                }
            }
        }
        return new Config(servletNameToServletClassNameMap, urlPatternToServletNameMap);
    }

}
