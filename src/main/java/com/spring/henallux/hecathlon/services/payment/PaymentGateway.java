package com.spring.henallux.hecathlon.services.payment;

import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentRedirect createPayment(String reference, BigDecimal amount, String returnUrl, String cancelUrl);

    PaymentResult capture(String paymentId);
}
