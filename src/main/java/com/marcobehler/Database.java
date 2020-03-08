package com.marcobehler;

import java.util.HashMap;
import java.util.Map;

public class Database {

    public static String theOnlyClientId = "my-first-client";

    public static String theOnlyClientSecret = "s3cr3t";

    public static String theOnlyClientRedirectUrl = "http://localhost:8080/callback";

    public static Map<String, Integer> authorizationCodes = new HashMap();

    public static Map<String, Integer> accessTokens = new HashMap();

    public static Map<String, User> users = new HashMap<>() {{
        put("marco", new User(1, "marco", "behler"));
    }};
}
