package com.spring.henallux.hecathlon.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderStatusTest {
    @Test
    void onlyPendingOrdersAreUnpaid() {
        assertFalse(OrderStatus.PENDING_PAYMENT.isPaid());
        assertTrue(OrderStatus.CONFIRMED.isPaid());
        assertTrue(OrderStatus.DELIVERED.isPaid());
    }

    @Test
    void onlyDeliveredOrdersAreFinished() {
        assertTrue(OrderStatus.PENDING_PAYMENT.isInProgress());
        assertTrue(OrderStatus.SHIPPED.isInProgress());
        assertFalse(OrderStatus.DELIVERED.isInProgress());
    }

    @Test
    void trackingStepsStartAfterPayment() {
        assertEquals(4, OrderStatus.TRACKING_STEPS.size());
        assertEquals(1, OrderStatus.CONFIRMED.getStep());
        assertEquals(4, OrderStatus.DELIVERED.getStep());
    }
}
