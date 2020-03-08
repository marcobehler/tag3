package com.marcobehler.keycloak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcobehler.Database;
import com.marcobehler.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KeycloakServlet extends HttpServlet {


    public Map<String, Object> lastRequest = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getRequestURI().equals("/login")) {
            lastRequest.clear();

            String clientId = req.getParameter("client_id");
            String redirectUri = req.getParameter("redirect_uri");
            String responseType = req.getParameter("response_type");
            String state = req.getParameter("state");

            if (clientId == null || redirectUri == null || responseType == null || state == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            lastRequest.put("clientId", clientId);
            lastRequest.put("redirectUri", redirectUri);
            lastRequest.put("responseType", responseType);
            lastRequest.put("state", state);

            KeycloakServlet.class.getResourceAsStream("/keycloak/login.html").transferTo(resp.getOutputStream());
        }
        else {
            resp.getWriter().print("<html>This is our keycloak sever</html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().equals("/login")) {
            handleLogin(req, resp);
        }
        else if (req.getRequestURI().equals("/authorize")) {
            handleAuthorization(req, resp);
        }
        else if (req.getRequestURI().equals("/token")) {
            handleIssueAccessToken(req, resp);
        }
        else {
            throw new IllegalStateException("Don't know what to do here.");
        }
    }


    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || password == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().print("Try again");
            return;
        }

        User user = Database.users.get(username);

        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().print("Try again");
            return;
        }

        if (!user.getPassword().equals(password)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().print("Try again");
            return;
        }


        lastRequest.put("userId", user.getId());

        InputStream authorize = KeycloakServlet.class.getResourceAsStream("/keycloak/authorize.html");
        String s = new String(authorize.readAllBytes(), "UTF-8");
        String clientId = (String) lastRequest.get("clientId");
        s = s.replace("${client}", clientId);

        resp.getWriter().print(s);
    }

    private void handleAuthorization(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String redirectUri = (String) lastRequest.get("redirectUri");

        // das is die juicy information
        String authorizationCode = UUID.randomUUID().toString();

        Database.authorizationCodes.put(authorizationCode, (Integer) lastRequest.get("userId"));

        resp.sendRedirect(redirectUri + "?code=" + authorizationCode + "&state=" + lastRequest.get("state"));
    }

    private void handleIssueAccessToken(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String authorization = req.getHeader("Authorization");

        if (authorization == null || authorization.isBlank()) {
            System.err.println("no authorization");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Unsupported Grant Type");
            return;
        }

        String base64Credentials = authorization.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);

        String clientId = values[0];
        String clientSecret = values[1];
        String grantType = req.getParameter("grant_type");
        String code = req.getParameter("code");


        if (!grantType.equals("authorization_code")) {
            System.err.println("Wrong grant type");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Unsupported Grant Type");
            return;
        }


        if (!clientId.equals(Database.theOnlyClientId)) {
            System.err.println("no such client registered");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("no such client");
            return;
        }

        if (!clientSecret.equals(Database.theOnlyClientSecret)) {
            System.err.println("wrong client secret");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("no such client");
            return;
        }


        final Integer userId = Database.authorizationCodes.get(code);

        if (userId == null) {
            System.err.println("no such user for authorization code: "+ code);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("no such client");
            return;
        }
        Database.authorizationCodes.remove(code);

        final String accessToken = UUID.randomUUID().toString();
        Database.accessTokens.put(accessToken, userId);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");

        System.out.println("Sending out bearer token");

        Map<String,String> map = new HashMap<>();
        map.put("access_token", accessToken);
        map.put("token_type", "Bearer");
        resp.getWriter().print(new ObjectMapper().writeValueAsString(map));
    }
}
