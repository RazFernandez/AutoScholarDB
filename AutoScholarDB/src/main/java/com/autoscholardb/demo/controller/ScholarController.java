package com.autoscholardb.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autoscholardb.demo.services.ScholarService;

@RestController
public class ScholarController {

    private final ScholarService scholarService;

    public ScholarController(ScholarService scholarService) {
        this.scholarService = scholarService;
    }

    @GetMapping("/api/scholar")
    public String fetchScholar(
            @RequestParam String authorId,
            @RequestParam String apiKey) {
        try {
            // Call service and block until result arrives
            scholarService.fetchAuthotArticlesApi(authorId, apiKey).join();

            // Just return a confirmation message to the HTTP client
            return "Check console for author data output.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}