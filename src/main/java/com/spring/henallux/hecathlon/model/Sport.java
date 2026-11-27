package com.spring.henallux.hecathlon.model;

public class Sport {

    // id is also the translation key suffix: sport.<id> / sport.<id>.tagline
    private String id;
    private String image;

    public Sport(String id, String image) {
        this.id = id;
        this.image = image;
    }

    public String getId() { return id; }

    public String getImage() { return image; }

}
