package com.spring.henallux.hecathlon.services;

import com.spring.henallux.hecathlon.dataAccess.dao.OrderDataAccess;
import com.spring.henallux.hecathlon.model.Cart;
import com.spring.henallux.hecathlon.model.CartLine;
import com.spring.henallux.hecathlon.model.DeliveryForm;
import com.spring.henallux.hecathlon.model.DeliveryMode;
import com.spring.henallux.hecathlon.model.Order;
import com.spring.henallux.hecathlon.model.OrderLine;
import com.spring.henallux.hecathlon.model.OrderStatus;
import com.spring.henallux.hecathlon.services.payment.PaymentGateway;
import com.spring.henallux.hecathlon.services.payment.PaymentRedirect;
import com.spring.henallux.hecathlon.services.payment.PaymentResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    public static final String CURRENCY = "EUR";

    private final OrderDataAccess orderDataAccess;
    private final PaymentGateway paymentGateway;

    @Autowired
    public OrderService(OrderDataAccess orderDataAccess, PaymentGateway paymentGateway) {
        this.orderDataAccess = orderDataAccess;
        this.paymentGateway = paymentGateway;
    }

    public BigDecimal deliveryCost(Cart cart, DeliveryMode mode) {
        return mode == DeliveryMode.STORE ? BigDecimal.ZERO : cart.getDeliveryCost();
    }

    public Order placeOrder(String customerEmail, Cart cart, DeliveryForm delivery) {
        if (cart.isEmpty()) {
            throw new IllegalStateException("Cannot order an empty cart");
        }
        LocalDateTime now = LocalDateTime.now();
        Order order = new Order();
        order.setCustomerEmail(customerEmail);
        order.setDate(now);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setStatusDate(now);
        order.setFirstName(delivery.getFirstName().trim());
        order.setLastName(delivery.getLastName().trim());
        order.setEmail(delivery.getEmail().trim());
        order.setPhone(delivery.getPhone());
        order.setDeliveryMode(delivery.getDeliveryMode());
        if (delivery.getDeliveryMode() == DeliveryMode.HOME) {
            order.setAddress(delivery.getAddress().trim());
            order.setAddressComplement(delivery.getAddressComplement());
            order.setPostalCode(delivery.getPostalCode());
            order.setCity(delivery.getCity().trim());
            order.setCountry(delivery.getCountry());
        }

        for (CartLine cartLine : cart.getLines()) {
            OrderLine line = new OrderLine();
            line.setProductId(cartLine.getProduct().getId());
            line.setProductName(cartLine.getProduct().getName());
            line.setBrand(cartLine.getProduct().getBrand());
            line.setImage(cartLine.getProduct().getImage());
            line.setSize(cartLine.getSize());
            line.setUnitPrice(cartLine.getProduct().getPrice());
            line.setQuantity(cartLine.getQuantity());
            order.getLines().add(line);
        }

        BigDecimal deliveryCost = deliveryCost(cart, delivery.getDeliveryMode());
        order.setSubtotal(cart.getSubtotal());
        order.setDeliveryCost(deliveryCost);
        order.setTotal(cart.getSubtotal().add(deliveryCost));
        return orderDataAccess.save(order);
    }

    public String startPayment(Order order, String returnUrl, String cancelUrl) {
        if (order.getStatus().isPaid()) {
            throw new IllegalStateException("Order already paid: " + order.getNumber());
        }
        PaymentRedirect redirect = paymentGateway.createPayment(order.getNumber(), order.getTotal(), returnUrl, cancelUrl);
        order.setPaymentReference(redirect.paymentId());
        orderDataAccess.save(order);
        return redirect.approvalUrl();
    }

    public boolean completePayment(Order order, String paymentId) {
        if (order.getStatus().isPaid()) {
            return true;
        }
        if (paymentId == null || !paymentId.equals(order.getPaymentReference())) {
            return false;
        }
        PaymentResult result = paymentGateway.capture(paymentId);
        if (!result.completed() || !CURRENCY.equals(result.currency()) || result.amount().compareTo(order.getTotal()) != 0) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        order.setStatus(OrderStatus.CONFIRMED);
        order.setStatusDate(now);
        order.setPaidAt(now);
        orderDataAccess.save(order);
        return true;
    }

    public List<Order> findByCustomer(String customerEmail) {
        return orderDataAccess.findByCustomerEmail(customerEmail);
    }

    public Optional<Order> findForCustomer(Long id, String customerEmail) {
        return Optional.ofNullable(orderDataAccess.findByIdAndCustomerEmail(id, customerEmail));
    }
}
