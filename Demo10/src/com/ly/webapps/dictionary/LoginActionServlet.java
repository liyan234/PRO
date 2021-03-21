package com.ly.webapps.dictionary;

import com.ly.standard.ServletException;
import com.ly.standard.http.HttpServlet;
import com.ly.standard.http.HttpServletRequest;
import com.ly.standard.http.HttpServletResponse;
import com.ly.standard.http.HttpSession;

import java.io.IOException;

public class LoginActionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        if (username.equals("peixinchen") && password.equals("123456")) {
            User user = new User(username, password);
            HttpSession session = req.getSession();
            session.setAttribute("user", user);

            resp.sendRedirect("profile-action");
        } else {
            resp.sendRedirect("login.html");
        }
    }
}
