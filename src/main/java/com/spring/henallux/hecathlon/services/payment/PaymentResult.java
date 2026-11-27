package com.spring.henallux.hecathlon.services.payment;

import java.math.BigDecimal;

public record PaymentResult(boolean completed, BigDecimal amount, String currency) {
}
