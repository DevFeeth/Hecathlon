package com.spring.henallux.hecathlon.model;

import java.util.Comparator;

/** Sort orders offered on a sport page. The URL value is the lower-case name (e.g. ?tri=price_asc). */
public enum ProductSort {

    RELEVANCE((a, b) -> 0), // keeps catalogue order
    PRICE_ASC(Comparator.comparing(Product::getPrice)),
    PRICE_DESC(Comparator.comparing(Product::getPrice).reversed()),
    RATING(Comparator.comparingDouble(Product::getRating).reversed());

    private final Comparator<Product> comparator;

    ProductSort(Comparator<Product> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Product> getComparator() { return comparator; }

    public String getParam() { return name().toLowerCase(); }

    /** Unknown or missing values fall back to RELEVANCE. */
    public static ProductSort fromParam(String param) {
        for (ProductSort sort : values()) {
            if (sort.getParam().equals(param)) {
                return sort;
            }
        }
        return RELEVANCE;
    }

}
