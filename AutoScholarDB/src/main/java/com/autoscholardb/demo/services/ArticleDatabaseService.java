package com.autoscholardb.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.autoscholardb.demo.model.Articles.Article; // Assuming your Article DTO is here
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

    /**
     * Maps an Article DTO to an ArticleEntity and saves it to the database.
     * 
     * @param articleDto The DTO received from the client (containing Article data).
     * @return The saved ArticleEntity.
     */
    public ArticleEntity saveArticle(Article articleDto) {
        ArticleEntity entity = new ArticleEntity();

        // 1. Direct DTO to Entity Mapping
        entity.setTitle(articleDto.getTitle());
        entity.setAuthors(articleDto.getAuthors());
        entity.setLink(articleDto.getLink());

        // 2. Mapping the publication fields into the single 'publicationDate' column
        // We combine publication venue and year into one string for the target column.
        String pubYear = (articleDto.getYear() != null && !articleDto.getYear().isEmpty()) ? ", " + articleDto.getYear()
                : "";
        String publicationInfo = (articleDto.getPublication() != null ? articleDto.getPublication()
                : "N/A Publication Info") + pubYear;
        entity.setPublicationDate(publicationInfo);

        // 3. Mapping nested CitedBy value (handling potential nulls)
        if (articleDto.getCitedBy() != null) {
            // Note: Assuming DTO's CitedBy returns an Integer for the database column.
            entity.setCitedBy(articleDto.getCitedBy().getValue());
        } else {
            entity.setCitedBy(0);
        }

        // Initialize optional/empty fields (abstract and keywords are not in API
        // response)
        entity.setAbstractText("");
        entity.setKeywords("");

        return articleRepository.save(entity);
    }
}