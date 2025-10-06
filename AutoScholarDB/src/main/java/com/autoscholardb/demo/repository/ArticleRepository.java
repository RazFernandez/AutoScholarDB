package com.autoscholardb.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.autoscholardb.demo.model.Articles.ArticleEntity;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {

    // Spring Data JPA automatically generates the query for findAll (equivalent to
    // SELECT * FROM scholarly_articles)
    List<ArticleEntity> findAll();
}
