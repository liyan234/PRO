package com.ly.webapps.dictionary;

import com.ly.standard.ServletException;
import com.ly.standard.http.HttpServlet;
import com.ly.standard.http.HttpServletRequest;
import com.ly.standard.http.HttpServletResponse;
import com.ly.standard.http.HttpSession;

import java.io.IOException;

public class ProfileActionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            resp.sendRedirect("login.html");
        } else {
            resp.setContentType("text/plain");
            resp.getWriter().println(user.toString());
        }
    }
}
