package com.neasaa.http;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
public class ApiRequest<T> {
    public static final String SESSION_COOKIE_NAME = "JSESSIONID";

    private String baseUrl;
    private String contextPath;
    @Builder.Default
    private HttpMethod method = HttpMethod.POST;
    private T requestBody;

    private Map<String, String> headers;
    private Map<String, String> cookies;

    public ApiRequest<T> addDefaultHeaders() {
        if(this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.put("Content-Type", "application/json");
        this.headers.put("Accept", "application/json");
        return this;
    }

    public ApiRequest<T> addSessionCookie(String sessionId) {
        if(this.cookies == null) {
            this.cookies = new HashMap<>();
        }
        this.cookies.put(SESSION_COOKIE_NAME, sessionId);
        return this;
    }

}
