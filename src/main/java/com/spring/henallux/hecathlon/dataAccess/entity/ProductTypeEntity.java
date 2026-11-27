package com.spring.henallux.hecathlon.dataAccess.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_type")
public class ProductTypeEntity {
    @Id
    @Column(name = "id", length = 30)
    private String id;

    @Column(name = "name_fr", nullable = false, length = 60)
    private String nameFr;

    @Column(name = "name_en", nullable = false, length = 60)
    private String nameEn;

    @Column(name = "position", nullable = false)
    private int position;

    public ProductTypeEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNameFr() { return nameFr; }
    public void setNameFr(String nameFr) { this.nameFr = nameFr; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}
