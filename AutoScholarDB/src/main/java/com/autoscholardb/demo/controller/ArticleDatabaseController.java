package com.autoscholardb.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autoscholardb.demo.model.Articles.ArticleEntity;
import com.autoscholardb.demo.services.ArticleDatabaseService;

@RestController
@RequestMapping("/db/articles") // Base path for database operations
public class ArticleDatabaseController {

    private final ArticleDatabaseService articleDatabaseService;

    public ArticleDatabaseController(ArticleDatabaseService articleDatabaseService) {
        this.articleDatabaseService = articleDatabaseService;
    }

    /**
     * Endpoint to fetch all articles stored in the PostgreSQL database.
     * 
     * @return A list of all articles (ArticleEntity) as JSON.
     */
    @GetMapping
    public ResponseEntity<List<ArticleEntity>> getAllArticles() {
        try {
            List<ArticleEntity> articles = articleDatabaseService.findAllArticles();
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            // Log the exception for server-side debugging
            System.err.println("Database fetch error: " + e.getMessage());
            // Return 500 status with an empty body or error structure
            return ResponseEntity.internalServerError().build();
        }
    }
}
