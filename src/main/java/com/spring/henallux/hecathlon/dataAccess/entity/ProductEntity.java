package com.spring.henallux.hecathlon.dataAccess.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class ProductEntity {
    @Id
    @Column(name = "id", length = 40)
    private String id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "brand", nullable = false, length = 60)
    private String brand;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sport_id", nullable = false)
    private SportEntity sport;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "type_id", nullable = false)
    private ProductTypeEntity type;

    @Column(name = "short_description", length = 120)
    private String description;

    @Column(name = "description", length = 1000)
    private String longDescription;

    @Column(name = "rating", nullable = false)
    private double rating;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "old_price", precision = 10, scale = 2)
    private BigDecimal oldPrice;

    @Column(name = "best_seller", nullable = false)
    private boolean bestSeller;

    @Column(name = "featured", nullable = false)
    private boolean featured;

    @Column(name = "highlighted", nullable = false)
    private boolean highlighted;

    @Column(name = "image")
    private String image;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_size", joinColumns = @JoinColumn(name = "product_id"))
    @OrderColumn(name = "position")
    @Column(name = "size", nullable = false, length = 10)
    private List<String> sizes = new ArrayList<>();

    @Column(name = "position", nullable = false)
    private int position;

    public ProductEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public SportEntity getSport() { return sport; }
    public void setSport(SportEntity sport) { this.sport = sport; }
    public ProductTypeEntity getType() { return type; }
    public void setType(ProductTypeEntity type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLongDescription() { return longDescription; }
    public void setLongDescription(String longDescription) { this.longDescription = longDescription; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getOldPrice() { return oldPrice; }
    public void setOldPrice(BigDecimal oldPrice) { this.oldPrice = oldPrice; }
    public boolean isBestSeller() { return bestSeller; }
    public void setBestSeller(boolean bestSeller) { this.bestSeller = bestSeller; }
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
    public boolean isHighlighted() { return highlighted; }
    public void setHighlighted(boolean highlighted) { this.highlighted = highlighted; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public List<String> getSizes() { return sizes; }
    public void setSizes(List<String> sizes) { this.sizes = sizes; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}
