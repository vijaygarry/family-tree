package com.neasaa.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
public class ApiClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public <T, R> ApiResponse<R> processRequest(
            ApiRequest<T> request,
            Class<R> responseClass
    ) throws Exception {

        String fullUrl = request.getBaseUrl() + request.getContextPath();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl));

        // Add headers
        request.getHeaders().forEach(requestBuilder::header);

        // Add cookies
        if (request.getCookies() != null && !request.getCookies().isEmpty()) {
            String cookieHeader = request.getCookies().entrySet()
                    .stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("; "));
            requestBuilder.header("Cookie", cookieHeader);
        }

        Map<String, List<String>> requestHeaders =
                requestBuilder.build().headers().map();


        // Handle HTTP method
        switch (request.getMethod()) {
            case GET -> requestBuilder.GET();
            case POST -> {
                String requestBody = objectMapper.writeValueAsString(request.getRequestBody());
                log.info("RequestBody=>{}", requestBody);
                requestBuilder.POST(
                    HttpRequest.BodyPublishers.ofString(
                            requestBody
                    ));
            }
            case PUT -> requestBuilder.PUT(
                    HttpRequest.BodyPublishers.ofString(
                            objectMapper.writeValueAsString(request.getRequestBody())
                    ));
            case DELETE -> requestBuilder.DELETE();
        }

        // ⏱ Start timing
        long startTime = System.currentTimeMillis();

        HttpResponse<String> response = httpClient.send(
                requestBuilder.build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // ⏱ End timing
        long endTime = System.currentTimeMillis();

        Map<String, String> cookies =
                CookieUtil.extractCookies(response.headers().map());

        return new ApiResponse<>(
                response.statusCode(),
                requestHeaders,
                response.headers().map(),
                cookies,
                response.body(),
                startTime,
                endTime,
                responseClass
        );
    }
}
