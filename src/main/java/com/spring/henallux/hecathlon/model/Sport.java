package com.spring.henallux.hecathlon.model;

public class Sport {
    private String id;
    private String nameFr;
    private String nameEn;
    private String taglineFr;
    private String taglineEn;
    private String descriptionFr;
    private String descriptionEn;
    private String image;
    private int position;

    public String getName(String language) { return "en".equals(language) ? nameEn : nameFr; }
    public String getTagline(String language) { return "en".equals(language) ? taglineEn : taglineFr; }
    public String getDescription(String language) { return "en".equals(language) ? descriptionEn : descriptionFr; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNameFr() { return nameFr; }
    public void setNameFr(String nameFr) { this.nameFr = nameFr; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }
    public String getTaglineFr() { return taglineFr; }
    public void setTaglineFr(String taglineFr) { this.taglineFr = taglineFr; }
    public String getTaglineEn() { return taglineEn; }
    public void setTaglineEn(String taglineEn) { this.taglineEn = taglineEn; }
    public String getDescriptionFr() { return descriptionFr; }
    public void setDescriptionFr(String descriptionFr) { this.descriptionFr = descriptionFr; }
    public String getDescriptionEn() { return descriptionEn; }
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}
