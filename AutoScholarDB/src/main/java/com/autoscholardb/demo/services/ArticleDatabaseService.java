package com.autoscholardb.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.autoscholardb.demo.model.Articles.ArticleEntity;
import com.autoscholardb.demo.repository.ArticleRepository;

@Service
public class ArticleDatabaseService {

    private final ArticleRepository articleRepository;

    // Dependency injection of the repository
    public ArticleDatabaseService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * Retrieves all scholarly articles from the database.
     * 
     * @return A list of ArticleEntity objects.
     */
    public List<ArticleEntity> findAllArticles() {
        return articleRepository.findAll();
    }
}
