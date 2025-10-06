package com.autoscholardb.demo.model.Articles;

public class Article {
    private String title;
    private String publication;
    private String link;
    private String year;
    private CitedBy citedBy; // Using the CitedBy DTO for the value
    private String authors; // Assumes authors is a single string as in your example

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public CitedBy getCitedBy() {
        return citedBy;
    }

    public void setCitedBy(CitedBy citedBy) {
        this.citedBy = citedBy;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }
}