package com.ly.webapps.dictionary;

import com.ly.standard.http.HttpServlet;
import com.ly.standard.http.HttpServletRequest;
import com.ly.standard.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class TranslateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("我是translate的doget方法");
        String english = req.getParameter("english");

        resp.setContentType("text/html");

        String chinese = translate(english);
        PrintWriter pw = resp.getWriter();
        pw.printf("<h1>翻译服务</h1>\r\n");
        pw.printf("<p>%s的意思是%s</p>", english, chinese);
        pw.flush();
    }

    private String translate(String english) {
        String chinese = english;
        return chinese;
    }
}
