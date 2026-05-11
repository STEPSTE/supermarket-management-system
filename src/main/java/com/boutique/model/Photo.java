package com.boutique.model;

import jakarta.persistence.*;

@Embeddable
public class Photo {
    
    @Column(name = "photo_url")
    private String url;
    
    @Column(name = "is_main")
    private Boolean isMain = false;

    public Photo() {}
    public Photo(String url) { this.url = url; this.isMain = false; }
    public Photo(String url, Boolean isMain) {
        this.url = url;
        this.isMain = isMain;
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Boolean getIsMain() { return isMain; }
    public void setIsMain(Boolean isMain) { this.isMain = isMain; }
}