package com.spring.henallux.hecathlon.services;

import com.spring.henallux.hecathlon.TestCatalog;
import com.spring.henallux.hecathlon.dataAccess.dao.OrderDataAccess;
import com.spring.henallux.hecathlon.model.Cart;
import com.spring.henallux.hecathlon.model.DeliveryForm;
import com.spring.henallux.hecathlon.model.DeliveryMode;
import com.spring.henallux.hecathlon.model.Order;
import com.spring.henallux.hecathlon.model.OrderStatus;
import com.spring.henallux.hecathlon.services.payment.PaymentGateway;
import com.spring.henallux.hecathlon.services.payment.PaymentRedirect;
import com.spring.henallux.hecathlon.services.payment.PaymentResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderDataAccess orderDataAccess;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        lenient().when(orderDataAccess.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private static DeliveryForm delivery(DeliveryMode mode) {
        DeliveryForm form = new DeliveryForm();
        form.setFirstName(" Alex ");
        form.setLastName("Martin");
        form.setEmail("alex@example.com");
        form.setPhone("0470123456");
        form.setDeliveryMode(mode);
        form.setAddress("Rue 1");
        form.setPostalCode("5000");
        form.setCity("Namur");
        return form;
    }

    private static Cart cartWithTwoBalls() {
        Cart cart = new Cart();
        cart.add(TestCatalog.match500(), null, 2);
        return cart;
    }

    private static Order pendingOrder(String reference) {
        Order order = new Order();
        order.setId(7L);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setTotal(new BigDecimal("54.97"));
        order.setPaymentReference(reference);
        return order;
    }

    @Test
    void orderIsSavedBeforePaymentWithHomeDeliveryCost() {
        Order order = orderService.placeOrder("alex@example.com", cartWithTwoBalls(), delivery(DeliveryMode.HOME));

        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        assertEquals(new BigDecimal("49.98"), order.getSubtotal());
        assertEquals(Cart.DELIVERY_COST, order.getDeliveryCost());
        assertEquals(new BigDecimal("54.97"), order.getTotal());
        assertEquals("Alex", order.getFirstName());
        assertEquals(2, order.getLines().getFirst().getQuantity());
        assertNull(order.getPaidAt());
        verify(orderDataAccess).save(order);
    }

    @Test
    void storePickupIsFreeAndKeepsNoAddress() {
        Order order = orderService.placeOrder("alex@example.com", cartWithTwoBalls(), delivery(DeliveryMode.STORE));

        assertEquals(BigDecimal.ZERO, order.getDeliveryCost());
        assertNull(order.getAddress());
    }

    @Test
    void emptyCartCannotBeOrdered() {
        assertThrows(IllegalStateException.class,
                () -> orderService.placeOrder("alex@example.com", new Cart(), delivery(DeliveryMode.HOME)));
    }

    @Test
    void startingPaymentKeepsThePayPalReference() {
        Order order = pendingOrder(null);
        when(paymentGateway.createPayment("HC000007", new BigDecimal("54.97"), "back", "cancel"))
                .thenReturn(new PaymentRedirect("PAYPAL-1", "https://paypal.test/approve"));

        String url = orderService.startPayment(order, "back", "cancel");

        assertEquals("https://paypal.test/approve", url);
        assertEquals("PAYPAL-1", order.getPaymentReference());
        verify(orderDataAccess).save(order);
    }

    @Test
    void completedPaymentWithTheRightAmountConfirmsTheOrder() {
        Order order = pendingOrder("PAYPAL-1");
        when(paymentGateway.capture("PAYPAL-1")).thenReturn(new PaymentResult(true, new BigDecimal("54.97"), "EUR"));

        assertTrue(orderService.completePayment(order, "PAYPAL-1"));

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertNotNull(order.getPaidAt());
    }

    @Test
    void paymentIdOfAnotherOrderIsRefusedWithoutCalling() {
        Order order = pendingOrder("PAYPAL-1");

        assertFalse(orderService.completePayment(order, "PAYPAL-2"));

        verify(paymentGateway, never()).capture(anyString());
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
    }

    @Test
    void wrongAmountOrIncompletePaymentIsRefused() {
        Order order = pendingOrder("PAYPAL-1");
        when(paymentGateway.capture("PAYPAL-1")).thenReturn(new PaymentResult(true, new BigDecimal("1.00"), "EUR"));
        assertFalse(orderService.completePayment(order, "PAYPAL-1"));

        when(paymentGateway.capture("PAYPAL-1")).thenReturn(new PaymentResult(false, new BigDecimal("54.97"), "EUR"));
        assertFalse(orderService.completePayment(order, "PAYPAL-1"));

        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        verify(orderDataAccess, never()).save(any());
    }

    @Test
    void alreadyPaidOrderIsNotCapturedTwice() {
        Order order = pendingOrder("PAYPAL-1");
        order.setStatus(OrderStatus.CONFIRMED);

        assertTrue(orderService.completePayment(order, "PAYPAL-1"));

        verify(paymentGateway, never()).capture(anyString());
        assertThrows(IllegalStateException.class, () -> orderService.startPayment(order, "back", "cancel"));
    }
}
