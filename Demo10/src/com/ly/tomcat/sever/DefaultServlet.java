package com.ly.tomcat.sever;

import com.ly.standard.ServletException;
import com.ly.standard.http.HttpServlet;
import com.ly.standard.http.HttpServletRequest;
import com.ly.standard.http.HttpServletResponse;
import com.ly.tomcat.HttpServer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DefaultServlet extends HttpServlet {
    private final String welcomeFile ="/index.html";
    private final Map<String, String> mine = new HashMap<>();
    // text/plain 为无格式正文
    private final String defaultContentType = "text/plain";


    @Override
    public void init() throws ServletException {
        mine.put("htm", "text/html");
        mine.put("html", "text/html");
        mine.put("jpg", "image/jpeg");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        // 处理
        System.out.println("由我处理静态资源");
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();
        //  证明未输入任何东西
        if (servletPath.trim().equals("/")) {
            servletPath = welcomeFile;
        }
        String fileName = String.format("%s\\%s%s", HttpServer.WEBAPPS_BASE, contextPath, servletPath);
        System.out.println("fileName" + fileName );
        File file = new File(fileName);
        if (!file.exists()) {
            // 文件不存在 由这个API来处理
            HttpServer.notFoundServlet.service(req, resp);
            return;
        }
        String contentType = getContentType(servletPath);
        resp.setContentType(contentType);

        OutputStream outputStream = resp.getOutputStream();
        try(InputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        }
    }

    private String getContentType(String servletPath) {
        String contentType = defaultContentType;
        int i = servletPath.lastIndexOf(".");
        if (i != -1) {
            String ext = servletPath.substring(i+1);
            contentType = mine.getOrDefault(ext, defaultContentType);
        }
        return contentType;
    }
}
