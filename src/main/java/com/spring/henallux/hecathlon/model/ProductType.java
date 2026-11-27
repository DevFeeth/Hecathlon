package com.spring.henallux.hecathlon.model;

public class ProductType {
    private String id;
    private String nameFr;
    private String nameEn;
    private int position;

    public String getName(String language) { return "en".equals(language) ? nameEn : nameFr; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNameFr() { return nameFr; }
    public void setNameFr(String nameFr) { this.nameFr = nameFr; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}
