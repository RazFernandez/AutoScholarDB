package com.autoscholardb.demo.model;

import java.util.List;

public class AuthorInfo {
    private String name;
    private String affiliations;
    private String email;
    private String website;
    private String thumbnail;
    private List<AuthorInterest> interests;

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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<AuthorInterest> getInterests() {
        return interests;
    }

    public void setInterests(List<AuthorInterest> interests) {
        this.interests = interests;
    }
}