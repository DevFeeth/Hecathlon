package com.spring.henallux.hecathlon.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartTest {

    private static Product product(String id, String price, String oldPrice) {
        return new Product(id, id, "brand", "running", "shoes", "", "", 0, 0,
                new BigDecimal(price), oldPrice == null ? null : new BigDecimal(oldPrice), false, null, List.of("M", "L"));
    }

    @Test
    void sameProductAndSizeIsMergedOtherSizeIsANewLine() {
        Cart cart = new Cart();
        Product shirt = product("shirt", "10.00", null);
        cart.add(shirt, "M", 1);
        cart.add(shirt, "M", 2);
        cart.add(shirt, "L", 1);

        assertEquals(2, cart.getLines().size());
        assertEquals(4, cart.getItemCount());
        assertEquals(new BigDecimal("40.00"), cart.getSubtotal());
    }

    @Test
    void quantityIsCappedAndZeroRemovesTheLine() {
        Cart cart = new Cart();
        cart.add(product("shirt", "10.00", null), "M", 25);
        assertEquals(Cart.MAX_QUANTITY, cart.getItemCount());

        cart.setQuantity("shirt_M", 0);
        assertTrue(cart.isEmpty());

        cart.setQuantity("unknown", 3); // ignored
        assertTrue(cart.isEmpty());
    }

    @Test
    void deliveryIsFreeFrom79Euros() {
        Cart cart = new Cart();
        cart.add(product("cheap", "78.99", null), null, 1);
        assertEquals(Cart.DELIVERY_COST, cart.getDeliveryCost());
        assertEquals(new BigDecimal("83.98"), cart.getTotal());
        assertEquals(new BigDecimal("0.01"), cart.getMissingForFreeDelivery());

        cart.add(product("more", "0.01", null), null, 1);
        assertEquals(BigDecimal.ZERO, cart.getDeliveryCost());
        assertEquals(BigDecimal.ZERO, cart.getMissingForFreeDelivery().stripTrailingZeros());

        assertEquals(BigDecimal.ZERO, new Cart().getDeliveryCost());
    }

    @Test
    void vatAndSavingsAreComputedOnTheTotal() {
        Cart cart = new Cart();
        cart.add(product("bike", "121.00", "150.00"), null, 1);

        assertEquals(new BigDecimal("21.00"), cart.getVat());
        assertEquals(new BigDecimal("29.00"), cart.getSavings());
    }

}
