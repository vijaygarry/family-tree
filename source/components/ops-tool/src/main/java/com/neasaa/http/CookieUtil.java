package com.neasaa.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CookieUtil {
    public static Map<String, String> extractCookies(Map<String, List<String>> headers) {
        Map<String, String> cookies = new HashMap<>();

        List<String> setCookies = headers.get("Set-Cookie");
        if (setCookies == null) {
            return cookies;
        }

        for (String header : setCookies) {
            String[] parts = header.split(";");
            if (parts.length > 0) {
                String[] cookie = parts[0].split("=", 2);
                if (cookie.length == 2) {
                    cookies.put(cookie[0], cookie[1]);
                }
            }
        }
        return cookies;
    }
}
