package com.autoscholardb.demo.model.Articles;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

// This entity maps directly to the PostgreSQL table
@Entity
@Table(name = "scholarly_articles")
public class ArticleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Uses database sequence for auto-increment
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "authors", nullable = false)
    private String authors;

    // This column holds the combined publication name and year (e.g., "Annals of
    // Physics, 1905")
    @Column(name = "publication_date")
    private String publicationDate;

    @Column(name = "abstract")
    private String abstractText; // Renamed to avoid keyword conflict

    @Column(name = "link")
    private String link;

    @Column(name = "keywords")
    private String keywords;

    @Column(name = "cited_by")
    private Integer citedBy; // Mapped to cited_by

    // Default constructor required by JPA
    public ArticleEntity() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Integer getCitedBy() {
        return citedBy;
    }

    public void setCitedBy(Integer citedBy) {
        this.citedBy = citedBy;
    }
}
