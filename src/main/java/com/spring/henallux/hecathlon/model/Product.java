package com.spring.henallux.hecathlon.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Product {

    private String id;
    private String name;
    private String brand;
    private String sportId;
    private String category;
    private String description;
    private String longDescription;
    private double rating;
    private int reviewCount;
    private BigDecimal price;
    private BigDecimal oldPrice;
    private boolean bestSeller;
    private String image;
    // empty when the product has a single size (ball, bottle, ...)
    private List<String> sizes;

    public Product(String id, String name, String brand, String sportId, String category, String description, String longDescription,
                   double rating, int reviewCount, BigDecimal price, BigDecimal oldPrice,
                   boolean bestSeller, String image, List<String> sizes) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.sportId = sportId;
        this.category = category;
        this.description = description;
        this.longDescription = longDescription;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.price = price;
        this.oldPrice = oldPrice;
        this.bestSeller = bestSeller;
        this.image = image;
        this.sizes = sizes;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public String getSportId() { return sportId; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getLongDescription() { return longDescription; }
    public double getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getOldPrice() { return oldPrice; }
    public boolean isBestSeller() { return bestSeller; }
    public String getImage() { return image; }
    public List<String> getSizes() { return sizes; }

    /** Reference shown on the product page, e.g. PACE-500. */
    public String getReference() { return id.toUpperCase(); }

    /** Discount rounded to the nearest percent, 0 when the product is not on sale. */
    public int getDiscountPercent() {
        if (oldPrice == null || oldPrice.compareTo(price) <= 0) {
            return 0;
        }
        return oldPrice.subtract(price)
                .multiply(BigDecimal.valueOf(100))
                .divide(oldPrice, 0, RoundingMode.HALF_UP)
                .intValue();
    }

}
