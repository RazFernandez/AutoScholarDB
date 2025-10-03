package com.autoscholardb.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autoscholardb.demo.services.ScholarService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RestController
public class ScholarController {

    private final ScholarService scholarService;

    public ScholarController(ScholarService scholarService) {
        this.scholarService = scholarService;
    }

@GetMapping("/api/scholar")
public ResponseEntity<?> fetchScholar(
        @RequestParam String authorId,
        @RequestParam String apiKey
) {
    try {
        JsonObject result = scholarService.fetchAuthorArticlesApi(authorId, apiKey).join();
        // Convert JsonObject → Map
        Map<String, Object> map = new Gson().fromJson(result, new com.google.gson.reflect.TypeToken<Map<String, Object>>(){}.getType());
        return ResponseEntity.ok(map);
    } catch (Exception e) {
        Map<String, String> error = Map.of("error", e.getMessage());
        return ResponseEntity.status(500).body(error);
    }
}
}