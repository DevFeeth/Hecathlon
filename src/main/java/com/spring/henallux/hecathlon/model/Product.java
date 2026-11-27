package com.spring.henallux.hecathlon.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private String id;
    private String name;
    private String brand;
    private String sportId;
    private ProductType type;
    private String description;
    private String longDescription;
    private double rating;
    private int reviewCount;
    private BigDecimal price;
    private BigDecimal oldPrice;
    private boolean bestSeller;
    private boolean featured;
    private boolean highlighted;
    private String image;
    private List<String> sizes = new ArrayList<>();
    private int position;

    public int getDiscountPercent() {
        if (oldPrice == null || oldPrice.compareTo(price) <= 0) {
            return 0;
        }
        return oldPrice.subtract(price)
                .multiply(BigDecimal.valueOf(100))
                .divide(oldPrice, 0, RoundingMode.HALF_UP)
                .intValue();
    }

    public String getReference() { return id.toUpperCase(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getSportId() { return sportId; }
    public void setSportId(String sportId) { this.sportId = sportId; }
    public ProductType getType() { return type; }
    public void setType(ProductType type) { this.type = type; }
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
