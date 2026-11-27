package com.spring.henallux.hecathlon.model;

import org.springframework.data.domain.Sort;

public enum ProductSort {
    RELEVANCE(Sort.by("position")),
    PRICE_ASC(Sort.by("price").and(Sort.by("position"))),
    PRICE_DESC(Sort.by(Sort.Direction.DESC, "price").and(Sort.by("position"))),
    RATING(Sort.by(Sort.Direction.DESC, "rating").and(Sort.by("position")));

    private final Sort sort;

    ProductSort(Sort sort) {
        this.sort = sort;
    }

    public Sort getSort() { return sort; }

    public String getParam() { return name().toLowerCase(); }

    public static ProductSort fromParam(String param) {
        for (ProductSort sort : values()) {
            if (sort.getParam().equals(param)) {
                return sort;
            }
        }
        return RELEVANCE;
    }
}
