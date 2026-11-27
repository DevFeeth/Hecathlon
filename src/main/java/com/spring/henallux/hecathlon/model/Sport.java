package com.spring.henallux.hecathlon.model;

import java.util.List;

public class Sport {

    // id is also the translation key suffix: sport.<id>, sport.<id>.tagline, ...
    private String id;
    private String image;
    // category ids, translated with category.<id>
    private List<String> categories;

    public Sport(String id, String image, List<String> categories) {
        this.id = id;
        this.image = image;
        this.categories = categories;
    }

    public String getId() { return id; }

    public String getImage() { return image; }

    public List<String> getCategories() { return categories; }

}
