package com.ly.tomcat.http;

import com.ly.standard.http.Cookie;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.*;

public class HttpRequestParser {
    public Request parse(InputStream inputStream) throws IOException, ClassNotFoundException {
        Scanner sc = new Scanner(inputStream, "UTF-8");
        // 解析request命令
        // 第一行  方法 路径 版本号
        // 方法头 key value 方式保存
        // 空行为结束
        /** 得到方法 GET*/
        String method = sc.next().toUpperCase();
        /** 得到路径http://baidu.com/yyyy/goodbye.html?target=xx
         *  http://baidu.com// uri ? queryString(以键值对的形式存在 & 作为分隔符)
         *  queryString(k1=v1&k2=v2&k3=v3)
         * */
        /** 解析得到URI*/
        String path = sc.next();
        String requestURI = path;
        /** 解析得到queryString*/
        Map<String, String> parameters = new HashMap<>();
        int i = path.indexOf("?");
        if (i != -1) {
            //重新覆盖URI
            requestURI = path.substring(0, i);
            String queryString = path.substring(i + 1);
            // 以键值对的形式插入到parameters中
            for (String kv : queryString.split("&")) {
                 String[] x = kv.split("=");
                 String k = URLDecoder.decode(x[0].trim());
                 String v = URLDecoder.decode(x[1].trim());
                 parameters.put(k,v);
            }
        }
        // 分开 contextPath 和 servletPath
        //http://baidu.com/yyyy/goodbye.html?target=xx 找到都第二个'/'
        // requestURI 为 /yyyy/goodbye.html
        int j = requestURI.indexOf('/', 1);
        String contextPath = "/"; // contextPath yyy 一般就是文件路径名
        String servletPath = requestURI; // servletPath就是 /goodbye.html
        if (j != -1) {
            contextPath = requestURI.substring(1, j);
            servletPath = requestURI.substring(j);
        }
        /** 解析版本号*/
        String version = sc.nextLine();
        // 那木我们的第一行正式解析完成了
        // 解析完成之后 我们需要解析对象头
        // 键值对的形式存在 ";" 作为分隔符 空行就结束
        Map<String, String> headers = new HashMap<>();
        // 对象头中有cookie 我们需要对cookie做特别解析
        List<Cookie> cookieList = new ArrayList<>();
        /** 开始解析对象头*/
        String line;
        while (sc.hasNext() && !(line = sc.nextLine().trim()).isEmpty()) {

            String[] kvParts = line.split(":");
            String name = kvParts[0].trim().toLowerCase();
            String value = kvParts[1].trim();
            headers.put(name, value);
            /** 解析cookie */
            if (name.equals("cookie") || name.equals("COOKIE")) {
                String[] cookieKV = value.split(";");
                for (String x : cookieKV) {
                    if (x.trim().isEmpty()) {
                        continue;
                    }
                    String[] kv = x.split("=");
                    String cookieName =  kv[0].trim();
                    String cookieValue = kv[1].trim();
                    Cookie cookie = new Cookie(cookieName, cookieValue);
                    cookieList.add(cookie);
                }
            }
        }
        // 对Request命令解析完成
        return new Request(method, requestURI, contextPath, servletPath, parameters, headers,cookieList);
    }
}
