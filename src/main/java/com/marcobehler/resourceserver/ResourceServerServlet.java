package com.marcobehler.resourceserver;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class ResourceServerServlet extends HttpServlet {

    private Map<Integer, List<Transaction>> transactionsDatabase = new HashMap<>();

    @Override
    public void init() throws ServletException {
        transactionsDatabase.put(1, List.of(new Transaction(1, "user-id-1-Mc Donalds", 10,  new Date()), new Transaction(2, "user-id-1-Starbucks", 5, new Date())));
        transactionsDatabase.put(2, List.of(new Transaction(3, "user-id-2-Burger King", 10, new Date()), new Transaction(4, "user-id-2San Francisco", 5, new Date())));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userIdString = req.getParameter("userId");

        if (userIdString == null || userIdString.isEmpty()) { // keine security :D
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().print("Goodbye");
            return;
        }

        Integer userId = Integer.valueOf(userIdString);

        if (req.getRequestURI().equals("/transactions")) {
            List<Transaction> transactions = transactionsDatabase.getOrDefault(userId, Collections.emptyList());
            resp.setContentType("application/json");
            resp.getWriter().print(new ObjectMapper().writeValueAsString(transactions));
        }
        else {
            resp.getWriter().print("<html> this is my credit card acquirer, like stripe or a 'bank' like paypal </html>");
        }
    }
}
