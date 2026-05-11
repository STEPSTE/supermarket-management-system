package com.boutique.model;

import jakarta.persistence.*;

@Embeddable
public class Photo {
    
    @Column(name = "photo_url")
    private String url;

    public Photo() {}
    public Photo(String url) { this.url = url; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}