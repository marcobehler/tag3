package com.marcobehler.clientapplication;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcobehler.resourceserver.Transaction;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

public class ClientApplicationServlet extends HttpServlet {

    private Integer currentUser = 1;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().equals("/invoices")) {

            HttpClient client = HttpClient.newBuilder()
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8082/transactions?userId=" + currentUser))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            try {
                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                final ObjectMapper objectMapper = new ObjectMapper();
                List<Transaction> sales = objectMapper.readValue(response.body(), new TypeReference<List<Transaction>>() {
                });


                List<PdfInvoice> invoices = sales.stream().map(s -> new PdfInvoice("http://www.marcobehler.com/invoices/" + s.description + ".pdf")).collect(Collectors.toList());
                resp.getWriter().print(objectMapper.writeValueAsString(invoices));
                resp.setContentType("application/json");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            ClientApplicationServlet.class.getResourceAsStream("/client/index.html").transferTo(resp.getOutputStream());
        }
    }
}