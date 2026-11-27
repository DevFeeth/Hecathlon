package com.spring.henallux.hecathlon.dataAccess.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sport")
public class SportEntity {
    @Id
    @Column(name = "id", length = 30)
    private String id;

    @Column(name = "name_fr", nullable = false, length = 60)
    private String nameFr;

    @Column(name = "name_en", nullable = false, length = 60)
    private String nameEn;

    @Column(name = "tagline_fr", length = 120)
    private String taglineFr;

    @Column(name = "tagline_en", length = 120)
    private String taglineEn;

    @Column(name = "description_fr", length = 500)
    private String descriptionFr;

    @Column(name = "description_en", length = 500)
    private String descriptionEn;

    @Column(name = "image")
    private String image;

    @Column(name = "position", nullable = false)
    private int position;

    public SportEntity() {}

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
