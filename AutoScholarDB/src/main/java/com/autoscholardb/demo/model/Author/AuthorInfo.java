package com.autoscholardb.demo.model.Author;

import java.util.List;

import com.autoscholardb.demo.model.Articles.Article;

public class AuthorInfo {
    private String name;
    private String affiliations;
    private String email;
    private String website;
    private List<AuthorInterest> interests;
    private List<Article> articles;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAffiliations() {
        return affiliations;
    }

    public void setAffiliations(String affiliations) {
        this.affiliations = affiliations;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<AuthorInterest> getInterests() {
        return interests;
    }

    public void setInterests(List<AuthorInterest> interests) {
        this.interests = interests;
    }

     // Getters and Setters for articles
    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}