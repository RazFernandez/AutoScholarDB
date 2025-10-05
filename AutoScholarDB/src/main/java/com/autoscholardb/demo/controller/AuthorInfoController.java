package com.autoscholardb.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autoscholardb.demo.model.Author.AuthorInfo;
import com.autoscholardb.demo.services.ScholarService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement; // Added for iteration

@RestController // Returns data (JSON), not a view/template
public class AuthorInfoController {

    private final ScholarService scholarService;
    private final Gson gson;

    public AuthorInfoController(ScholarService scholarService) {
        this.scholarService = scholarService;
        this.gson = new Gson();
    }

    @GetMapping("/api/scholar")
    public ResponseEntity<?> fetchScholar(
            @RequestParam String authorId) {
        try {
            // 1. Get the raw JsonObject from the service
            JsonObject result = scholarService.fetchAuthorArticlesApi(authorId).join();

            /*
             * * FIX: The AuthorInfo DTO expects 'name' and 'affiliations' at the root,
             * but the API response nests them under "author". We manually flatten the
             * structure here before deserializing to the DTO.
             */

            // 2. Extract the necessary components
            JsonObject authorData = result.getAsJsonObject("author");
            JsonArray articlesArray = result.getAsJsonArray("articles");

            // 3. Create a new JsonObject that matches the DTO's flat structure
            JsonObject flattenedJson = new JsonObject();

            // Copy properties from the nested "author" object to the root
            if (authorData != null) {
                // Copy all properties (name, affiliations, email, interests, etc.)
                authorData.entrySet().forEach(entry -> {
                    flattenedJson.add(entry.getKey(), entry.getValue());
                });
            }

            // 4. Transform and add the "articles" array back to the root level
            if (articlesArray != null) {
                JsonArray fixedArticlesArray = new JsonArray();

                for (JsonElement element : articlesArray) {
                    JsonObject article = element.getAsJsonObject();

                    // FIX: Rename nested "cited_by" key to match the DTO field "citedBy"
                    // (camelCase)
                    if (article.has("cited_by")) {
                        JsonObject citedByData = article.getAsJsonObject("cited_by");
                        article.remove("cited_by");
                        article.add("citedBy", citedByData); // Renamed key to camelCase
                    }

                    fixedArticlesArray.add(article);
                }

                flattenedJson.add("articles", fixedArticlesArray);
            }

            // 5. Deserialize the newly structured JsonObject into your AuthorInfo DTO
            AuthorInfo authorInfo = gson.fromJson(flattenedJson, AuthorInfo.class);

            // 6. Return the populated DTO. Spring handles JSON serialization.
            return ResponseEntity.ok(authorInfo);

        } catch (Exception e) {
            // Handle exceptions and return an appropriate HTTP status/body
            String errorMessage = "Error fetching author data: " + e.getMessage();
            System.err.println("API Controller Error: " + errorMessage);
            return ResponseEntity.status(500).body(java.util.Map.of("error", errorMessage));
        }
    }
}
