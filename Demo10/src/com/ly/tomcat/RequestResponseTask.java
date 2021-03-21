package com.ly.tomcat;


import com.ly.standard.Servlet;
import com.ly.standard.ServletException;
import com.ly.tomcat.http.HttpRequestParser;
import com.ly.tomcat.http.Request;
import com.ly.tomcat.http.Response;

import java.io.*;
import java.net.Socket;
import java.util.Map;

// 主要做的就是接受请求 解析请求 然后发送响应
public class RequestResponseTask implements Runnable {

    private final static HttpRequestParser parser = new HttpRequestParser();
    private final Socket socket;

    public RequestResponseTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            // 1. 解析并且得到请求对象
            Request request = parser.parse(socket.getInputStream());
            System.out.println(request);
           /* OutputStream os = socket.getOutputStream();
            Writer writer = new OutputStreamWriter(os, "UTF-8");
            PrintWriter printWriter = new PrintWriter(writer);
            printWriter.printf("HTTP/1.0 200 OK \r\n");
            printWriter.printf("\r\n");
            printWriter.flush();*/
            // 2. 实例化一个响应对象
            Response response = new Response();
            // 3. 根据URL得到 ContextPath也就是request.getContextPath()找到那个Context去处理
            Context contextHandle = HttpServer.defaultContext;
            for (Context context : HttpServer.contextList) {
                // 我们初始化的时候解析了context 然后将request请求中的contextPath进行对比
                // 看是否有对应的context对象
                if (context.getName().equals(request.getContextPath())) {
                    contextHandle = context;
                    break;
                }
            }
            System.out.println(contextHandle);
            // 4. 根据request.getServletPath找到Context中那个HttpServlet处理
            Servlet servlet = contextHandle.getServlet(request.getServletPath());
            // == null的时候 代表为servlet未找到 可能处理的就是静态资源
            if (servlet == null) {
                servlet = HttpServer.defaultServlet;
            }
            System.out.println("servlet : "+ servlet);
            // 5. 调用servlet.service(request， response) 交给业务层去处理
            servlet.service(request, response);
            System.out.println( "123" + response);
            // 6. 根据response中的对象,发送http响应。
            sendResponse(socket.getOutputStream(), response);

        } catch (IOException | ClassNotFoundException | ServletException e) {
            e.printStackTrace(System.out);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendResponse(OutputStream outputStream, Response response) throws IOException {
        // 1.保存session
        // 2.种cookie
        // 3.将session保存成文件
      /*  if (request.session != null) {
            Cookie cookie = new Cookie("session-id",request.session.sessionId);
            response.addCookie(cookie);
            //保存session
            request.session.setSessionSave();
        }*/


        Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
        PrintWriter printWriter = new PrintWriter(writer);
     /*   for (Cookie cookie : response.cookieList) {
            response.setHeader("Set-Cookie", String.format("%s=%s", cookie.getName(),cookie.getValue()));
        }
*/
        printWriter.printf("HTTP/1.0 %d\r\n",response.status);
        for(Map.Entry<String, String> entry : response.headers.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            printWriter.printf("%s: %s\r\n" ,name, value);
        }


        printWriter.printf("\r\n");

        System.out.println("567" + response);
        response.pw.flush();
//        response.bodyOutputStream.flush();
        response.bodyOutputStream.flush();
        printWriter.flush();

        //
      //  System.out.println("789" + response);

        byte[] bytes = response.bodyOutputStream.toByteArray();
        outputStream.write(bytes);
        outputStream.flush();
    }
}
