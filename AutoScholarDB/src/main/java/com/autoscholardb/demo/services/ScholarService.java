package com.autoscholardb.demo.services;

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
import com.google.gson.JsonSyntaxException;

/**
 * Service to fetch Google Scholar Author data via SerpAPI.
 * Method: fetchAuthotArticlesApi(String authorId, String apiKey)
 *
 * - Uses Java 11+ HttpClient (native)
 * - Uses Gson for JSON parsing / pretty printing
 * - Returns CompletableFuture<Void> and prints output to console
 */
@Service
public class ScholarService {

    private final HttpClient httpClient;
    private final Gson gson;

    public ScholarService() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Fetch author + articles from SerpAPI (google_scholar_author).
     * Prints success message (200) and pretty JSON to console.
     * Manages cases: 200 (Success), 200 but missing author, 401/403 (bad key), 404 (not found), other errors.
     *
     * @param authorId Google Scholar author id (e.g. 4bahYMkAAAAJ)
     * @param apiKey   SerpAPI API key
     * @return CompletableFuture<Void> that completes when the response has been printed (or exceptionally on immediate validation error)
     */
    public CompletableFuture<Void> fetchAuthotArticlesApi(String authorId, String apiKey) {
        // Basic validation
        if (authorId == null || authorId.isBlank()) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalArgumentException("authorId is required and cannot be blank"));
            return failed;
        }
        if (apiKey == null || apiKey.isBlank()) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalArgumentException("apiKey is required and cannot be blank"));
            return failed;
        }

        try {
            String encodedAuthorId = URLEncoder.encode(authorId, StandardCharsets.UTF_8);
            String encodedApiKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

            String url = String.format(
                    "https://serpapi.com/search.json?engine=google_scholar_author&author_id=%s&api_key=%s&hl=en",
                    encodedAuthorId, encodedApiKey
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            // async call — returns CompletableFuture<Void>
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        int status = response.statusCode();
                        String body = response.body() == null ? "" : response.body();

                        if (status == 200) {
                            // try to parse the JSON to decide if author exists
                            try {
                                JsonElement je = JsonParser.parseString(body);
                                JsonObject root = je.getAsJsonObject();

                                String apiStatus = null;
                                if (root.has("search_metadata")
                                        && root.getAsJsonObject("search_metadata").has("status")) {
                                    apiStatus = root.getAsJsonObject("search_metadata").get("status").getAsString();
                                }

                                if ("Success".equalsIgnoreCase(apiStatus) && root.has("author")) {
                                    System.out.println("200 OK - Operation successful.");
                                    System.out.println("Retrieved author data (pretty-printed JSON):");
                                    System.out.println(gson.toJson(je));
                                } else {
                                    System.out.println("200 OK but the API returned a non-success status or no author was found.");
                                    System.out.printf("search_metadata.status = %s%n", apiStatus);
                                    System.out.println("Full response:");
                                    System.out.println(gson.toJson(je));
                                }
                            } catch (JsonSyntaxException jse) {
                                System.err.println("200 OK but failed to parse JSON response: " + jse.getMessage());
                                System.out.println("Raw body:");
                                System.out.println(body);
                            } catch (Exception ex) {
                                System.err.println("Unexpected parsing error: " + ex.getMessage());
                                ex.printStackTrace();
                            }
                        } else if (status == 401 || status == 403) {
                            System.err.printf("Authentication/authorization failed (HTTP %d). Check your API key.%n", status);
                            // print server response body if any
                            if (!body.isBlank()) {
                                try {
                                    JsonElement je = JsonParser.parseString(body);
                                    System.err.println(gson.toJson(je));
                                } catch (Exception e) {
                                    System.err.println("Response body: " + body);
                                }
                            }
                        } else if (status == 404) {
                            System.err.println("404 Not Found. The author id may not exist.");
                            System.err.println("Response body: " + body);
                        } else {
                            System.err.printf("Unexpected HTTP status: %d%n", status);
                            if (!body.isBlank()) {
                                try {
                                    JsonElement je = JsonParser.parseString(body);
                                    System.err.println(gson.toJson(je));
                                } catch (Exception e) {
                                    System.err.println("Response body: " + body);
                                }
                            }
                        }
                    })
                    .exceptionally(ex -> {
                        System.err.println("Exception while calling SerpAPI: " + ex.getMessage());
                        ex.printStackTrace();
                        return null;
                    });

        } catch (Exception e) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(e);
            return failed;
        }
    }
}
