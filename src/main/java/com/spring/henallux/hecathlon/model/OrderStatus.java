package com.spring.henallux.hecathlon.model;

import java.util.List;

public enum OrderStatus {
    PENDING_PAYMENT,
    CONFIRMED,
    PREPARED,
    SHIPPED,
    DELIVERED;

    public static final List<OrderStatus> TRACKING_STEPS = List.of(CONFIRMED, PREPARED, SHIPPED, DELIVERED);

    public boolean isPaid() { return this != PENDING_PAYMENT; }

    public boolean isInProgress() { return this != DELIVERED; }

    public int getStep() { return ordinal(); }
}
