package com.neasaa.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ApiResponse<T> {
    private final int httpStatusCode;
    private final Map<String, List<String>> requestHeaders;
    private final Map<String, List<String>> responseHeaders;
    private final Map<String, String> cookies;
    private final String responseBody;

    // Timing metrics
    private final long requestStartTimeMillis;
    private final long requestEndTimeMillis;
    private final long durationMillis;

    private final Class<T> responseClass;
    private T responseObject;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public ApiResponse(
            int httpStatusCode,
            Map<String, List<String>> requestHeaders,
            Map<String, List<String>> responseHeaders,
            Map<String, String> cookies,
            String responseBody,
            long requestStartTimeMillis,
            long requestEndTimeMillis,
            Class<T> responseClass
    ) {
        this.httpStatusCode = httpStatusCode;
        this.requestHeaders = requestHeaders;
        this.responseHeaders = responseHeaders;
        this.cookies = cookies;
        this.responseBody = responseBody;
        this.requestStartTimeMillis = requestStartTimeMillis;
        this.requestEndTimeMillis = requestEndTimeMillis;
        this.durationMillis = requestEndTimeMillis - requestStartTimeMillis;
        this.responseClass = responseClass;
    }

    /**
     * Lazily deserialize JSON response body into response object
     */
    public T getResponse() {
        if (responseObject == null && responseBody != null && !responseBody.isBlank()) {
            try {
                responseObject = OBJECT_MAPPER.readValue(responseBody, responseClass);
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize response body", e);
            }
        }
        return responseObject;
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }
}
