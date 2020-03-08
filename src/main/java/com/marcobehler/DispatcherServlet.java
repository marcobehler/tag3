package com.marcobehler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DispatcherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().equals("/admin")) {
            resp.getWriter().print("das ist die admin area");
        } else if (req.getRequestURI().equals("/callcenter")) {
            resp.getWriter().print("das ist die area fuer callcenter mitarbeiter");
        } else {
            resp.getWriter().print("Hallo");
        }

    }
}
