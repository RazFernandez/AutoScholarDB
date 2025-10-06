package com.autoscholardb.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autoscholardb.demo.model.Articles.Article;
import com.autoscholardb.demo.model.Articles.ArticleEntity;
import com.autoscholardb.demo.services.ArticleDatabaseService;

/**
 * Controller for handling article saving operations via POST requests.
 */
@RestController
@RequestMapping("/db")
public class ArticleSaveController {

    private final ArticleDatabaseService articleDatabaseService;

    public ArticleSaveController(ArticleDatabaseService articleDatabaseService) {
        this.articleDatabaseService = articleDatabaseService;
    }

    /**
     * Endpoint to save a single article DTO into the PostgreSQL database.
     * Maps to: POST http://localhost:8080/db/save
     * 
     * @param articleDto The Article DTO object received from the client (JS
     *                   frontend).
     * @return The saved ArticleEntity with its new database ID.
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveArticle(@RequestBody Article articleDto) {
        try {
            // Simple validation: ensure we at least have a title
            if (articleDto.getTitle() == null || articleDto.getTitle().isEmpty()) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Article title cannot be empty."));
            }

            ArticleEntity savedEntity = articleDatabaseService.saveArticle(articleDto);
            // Return 201 Created status
            return ResponseEntity.status(201).body(savedEntity);
        } catch (Exception e) {
            System.err.println("Error saving article to database: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(java.util.Map.of("error", "Could not save article: " + e.getMessage()));
        }
    }
}