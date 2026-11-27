package com.spring.henallux.hecathlon.model;

import java.math.BigDecimal;

public class CartLine {

    private final String key;
    private final Product product;
    // null when the product has a single size
    private final String size;
    private int quantity;

    public CartLine(String key, Product product, String size, int quantity) {
        this.key = key;
        this.product = product;
        this.size = size;
        this.quantity = quantity;
    }

    public String getKey() { return key; }
    public Product getProduct() { return product; }
    public String getSize() { return size; }
    public int getQuantity() { return quantity; }
    void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isAtMaxQuantity() { return quantity >= Cart.MAX_QUANTITY; }

    public BigDecimal getTotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

}
