package com.spring.henallux.hecathlon;

import com.spring.henallux.hecathlon.model.Product;
import com.spring.henallux.hecathlon.model.ProductType;
import com.spring.henallux.hecathlon.model.Sport;

import java.math.BigDecimal;
import java.util.List;

public final class TestCatalog {
    public static final ProductType SHOES = type("shoes", "Chaussures", "Shoes", 1);
    public static final ProductType CLOTHING = type("clothing", "Textile", "Clothing", 2);
    public static final ProductType BALLS = type("balls", "Ballons", "Balls", 4);

    private TestCatalog() {}

    public static Sport sport(String id, String nameFr, String nameEn, int position) {
        Sport sport = new Sport();
        sport.setId(id);
        sport.setNameFr(nameFr);
        sport.setNameEn(nameEn);
        sport.setTaglineFr("Accroche " + nameFr);
        sport.setTaglineEn("Tagline " + nameEn);
        sport.setDescriptionFr("Description " + nameFr);
        sport.setDescriptionEn("Description " + nameEn);
        sport.setImage("/images/sports/" + id + ".jpg");
        sport.setPosition(position);
        return sport;
    }

    public static List<Sport> sports() {
        return List.of(running(), sport("football", "Football", "Football", 2), sport("cycling", "Cyclisme", "Cycling", 3));
    }

    public static Sport running() {
        return sport("running", "Course à pied", "Running", 1);
    }

    public static ProductType type(String id, String nameFr, String nameEn, int position) {
        ProductType type = new ProductType();
        type.setId(id);
        type.setNameFr(nameFr);
        type.setNameEn(nameEn);
        type.setPosition(position);
        return type;
    }

    public static Product product(String id, String name, String sportId, ProductType type, String price, String oldPrice, String... sizes) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setBrand("Élan");
        product.setSportId(sportId);
        product.setType(type);
        product.setDescription("Points forts " + id);
        product.setLongDescription("Description de " + name);
        product.setRating(4.5);
        product.setReviewCount(10);
        product.setPrice(new BigDecimal(price));
        product.setOldPrice(oldPrice == null ? null : new BigDecimal(oldPrice));
        product.setImage("/images/products/" + id + ".jpg");
        product.setSizes(List.of(sizes));
        return product;
    }

    public static Product pace500() {
        return product("pace-500", "Chaussures running homme Pace 500", "running", SHOES, "89.99", "109.99",
                "39", "40", "41", "42", "43", "44", "45", "46");
    }

    public static Product air100() {
        Product product = product("air-100", "T-shirt running homme Air 100", "running", CLOTHING, "19.99", null, "XS", "S", "M", "L", "XL");
        product.setBestSeller(true);
        return product;
    }

    public static Product match500() {
        return product("match-500", "Ballon de football Match 500 taille 5", "football", BALLS, "24.99", "29.99");
    }
}
