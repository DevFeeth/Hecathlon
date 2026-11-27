package com.spring.henallux.hecathlon.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductTest {

    private static Product product(String price, String oldPrice) {
        return new Product("id", "name", "brand", "running", "", 0, 0,
                new BigDecimal(price), oldPrice == null ? null : new BigDecimal(oldPrice), false, null);
    }

    @Test
    void discountIsRoundedToNearestPercent() {
        assertEquals(18, product("89.99", "109.99").getDiscountPercent());
        assertEquals(17, product("24.99", "29.99").getDiscountPercent());
        assertEquals(20, product("79.99", "99.99").getDiscountPercent());
    }

    @Test
    void noDiscountWithoutHigherOldPrice() {
        assertEquals(0, product("59.99", null).getDiscountPercent());
        assertEquals(0, product("59.99", "59.99").getDiscountPercent());
    }

}
