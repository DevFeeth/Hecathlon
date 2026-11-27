package com.spring.henallux.hecathlon.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/** Shopping cart kept in the HTTP session (see Constants.CART). Prices include VAT. */
public class Cart {

    public static final int MAX_QUANTITY = 10;
    public static final BigDecimal FREE_DELIVERY_FROM = new BigDecimal("79.00");
    public static final BigDecimal DELIVERY_COST = new BigDecimal("4.99");
    public static final BigDecimal VAT_RATE = new BigDecimal("0.21"); // Belgium

    // one line per product + size, in the order they were added
    private final Map<String, CartLine> lines = new LinkedHashMap<>();

    public void add(Product product, String size, int quantity) {
        String key = product.getId() + (size == null ? "" : "_" + size);
        CartLine line = lines.computeIfAbsent(key, k -> new CartLine(k, product, size, 0));
        line.setQuantity(Math.min(line.getQuantity() + quantity, MAX_QUANTITY));
    }

    /** A quantity of 0 or less removes the line; unknown keys are ignored. */
    public void setQuantity(String key, int quantity) {
        CartLine line = lines.get(key);
        if (line == null) {
            return;
        }
        if (quantity <= 0) {
            lines.remove(key);
        } else {
            line.setQuantity(Math.min(quantity, MAX_QUANTITY));
        }
    }

    public void remove(String key) {
        lines.remove(key);
    }

    public Collection<CartLine> getLines() { return lines.values(); }

    public boolean isEmpty() { return lines.isEmpty(); }

    public int getItemCount() {
        return lines.values().stream().mapToInt(CartLine::getQuantity).sum();
    }

    public BigDecimal getSubtotal() {
        return lines.values().stream().map(CartLine::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getDeliveryCost() {
        return isEmpty() || getSubtotal().compareTo(FREE_DELIVERY_FROM) >= 0 ? BigDecimal.ZERO : DELIVERY_COST;
    }

    /** What is still missing to get free delivery (0 when it is already free). */
    public BigDecimal getMissingForFreeDelivery() {
        return FREE_DELIVERY_FROM.subtract(getSubtotal()).max(BigDecimal.ZERO);
    }

    public BigDecimal getTotal() {
        return getSubtotal().add(getDeliveryCost());
    }

    /** VAT included in the total: total - total / (1 + rate). */
    public BigDecimal getVat() {
        BigDecimal total = getTotal();
        return total.subtract(total.divide(BigDecimal.ONE.add(VAT_RATE), 2, RoundingMode.HALF_UP));
    }

    /** Sum of the discounts on the lines (old price - price). */
    public BigDecimal getSavings() {
        return lines.values().stream()
                .filter(line -> line.getProduct().getOldPrice() != null)
                .map(line -> line.getProduct().getOldPrice().subtract(line.getProduct().getPrice())
                        .multiply(BigDecimal.valueOf(line.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
