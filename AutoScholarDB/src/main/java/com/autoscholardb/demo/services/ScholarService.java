package com.autoscholardb.demo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Service to fetch Google Scholar Author data via SerpAPI.
 * Method: fetchAuthorArticlesApi(String authorId)
 *
 * - Uses Java 11+ HttpClient (native)
 * - Uses Gson for JSON parsing
 * - Returns CompletableFuture<JsonObject> so it can be mapped to a model later
 * - SerpAPI key is injected via Spring's @Value, eliminating it as a method
 * parameter.
 */
@Service
public class ScholarService {

    private final HttpClient httpClient;
    @SuppressWarnings("unused")
    private final Gson gson;

    // 1. Inject the API key from application.properties
    @Value("${serp.api.key}")
    private String serpApiKey;

    public ScholarService() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Fetch author + articles from SerpAPI (google_scholar_author).
     *
     * @param authorId Google Scholar author id (e.g. 4bahYMkAAAAJ)
     * @return CompletableFuture<JsonObject> with the parsed JSON response
     */
    // 2. Removed 'String apiKey' from the method signature
    public CompletableFuture<JsonObject> fetchAuthorArticlesApi(String authorId) {
        if (authorId == null || authorId.isBlank()) {
            CompletableFuture<JsonObject> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalArgumentException("authorId is required and cannot be blank"));
            return failed;
        }

        // 3. Check the injected key (important for safety)
        if (serpApiKey == null || serpApiKey.isBlank()) {
            CompletableFuture<JsonObject> failed = new CompletableFuture<>();
            failed.completeExceptionally(
                    new IllegalStateException("SerpAPI key is not configured. Check 'serp.api.key' property."));
            return failed;
        }

        try {
            String encodedAuthorId = URLEncoder.encode(authorId, StandardCharsets.UTF_8);
            // 4. Use the injected serpApiKey directly
            String encodedApiKey = URLEncoder.encode(serpApiKey, StandardCharsets.UTF_8);

            String url = String.format(
                    "https://serpapi.com/search.json?engine=google_scholar_author&author_id=%s&api_key=%s&hl=en",
                    encodedAuthorId, encodedApiKey);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        int status = response.statusCode();
                        String body = response.body() == null ? "" : response.body();

                        if (status == 200) {
                            try {
                                JsonElement je = JsonParser.parseString(body);
                                JsonObject root = je.getAsJsonObject();
                                return root;
                            } catch (Exception ex) {
                                throw new RuntimeException("Failed to parse JSON response: " + ex.getMessage(), ex);
                            }
                        } else if (status == 401 || status == 403) {
                            throw new RuntimeException("Authentication/authorization failed (HTTP " + status
                                    + "). Check your API key. Response: " + body);
                        } else if (status == 404) {
                            throw new RuntimeException("404 Not Found. The author id may not exist. Response: " + body);
                        } else {
                            throw new RuntimeException("Unexpected HTTP status: " + status + " Response: " + body);
                        }
                    })
                    .exceptionally(ex -> {
                        throw new RuntimeException("Exception while calling SerpAPI: " + ex.getMessage(), ex);
                    });

        } catch (Exception e) {
            CompletableFuture<JsonObject> failed = new CompletableFuture<>();
            failed.completeExceptionally(e);
            return failed;
        }
    }
}