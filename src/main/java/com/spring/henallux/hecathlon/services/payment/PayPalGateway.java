package com.spring.henallux.hecathlon.services.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
public class PayPalGateway implements PaymentGateway {
    static final String CURRENCY = "EUR";

    private final RestClient restClient;
    private final String clientId;
    private final String clientSecret;

    @Autowired
    public PayPalGateway(@Value("${hecathlon.paypal.base-url}") String baseUrl,
                         @Value("${hecathlon.paypal.client-id}") String clientId,
                         @Value("${hecathlon.paypal.client-secret}") String clientSecret) {
        this(RestClient.builder().baseUrl(baseUrl), clientId, clientSecret);
    }

    PayPalGateway(RestClient.Builder builder, String clientId, String clientSecret) {
        this.restClient = builder.build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public PaymentRedirect createPayment(String reference, BigDecimal amount, String returnUrl, String cancelUrl) {
        Map<String, Object> body = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(Map.of(
                        "reference_id", reference,
                        "amount", Map.of("currency_code", CURRENCY, "value", amount.toPlainString()))),
                "payment_source", Map.of("paypal", Map.of("experience_context", Map.of(
                        "brand_name", "Hecathlon",
                        "shipping_preference", "NO_SHIPPING",
                        "user_action", "PAY_NOW",
                        "return_url", returnUrl,
                        "cancel_url", cancelUrl))));
        Map<?, ?> response = call(() -> restClient.post()
                .uri("/v2/checkout/orders")
                .headers(headers -> headers.setBearerAuth(accessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class));
        String approvalUrl = ((List<?>) response.get("links")).stream()
                .map(link -> (Map<?, ?>) link)
                .filter(link -> "payer-action".equals(link.get("rel")) || "approve".equals(link.get("rel")))
                .map(link -> (String) link.get("href"))
                .findFirst()
                .orElseThrow(() -> new PaymentException("PayPal did not return an approval link"));
        return new PaymentRedirect((String) response.get("id"), approvalUrl);
    }

    @Override
    public PaymentResult capture(String paymentId) {
        Map<?, ?> response = call(() -> restClient.post()
                .uri("/v2/checkout/orders/{id}/capture", paymentId)
                .headers(headers -> headers.setBearerAuth(accessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(Map.class));
        Map<?, ?> unit = (Map<?, ?>) ((List<?>) response.get("purchase_units")).getFirst();
        Map<?, ?> capture = (Map<?, ?>) ((List<?>) ((Map<?, ?>) unit.get("payments")).get("captures")).getFirst();
        Map<?, ?> amount = (Map<?, ?>) capture.get("amount");
        boolean completed = "COMPLETED".equals(response.get("status")) && "COMPLETED".equals(capture.get("status"));
        return new PaymentResult(completed, new BigDecimal((String) amount.get("value")), (String) amount.get("currency_code"));
    }

    private String accessToken() {
        if (!StringUtils.hasText(clientId) || !StringUtils.hasText(clientSecret)) {
            throw new PaymentException("PayPal credentials are not configured");
        }
        Map<?, ?> response = call(() -> restClient.post()
                .uri("/v1/oauth2/token")
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=client_credentials")
                .retrieve()
                .body(Map.class));
        return (String) response.get("access_token");
    }

    private static Map<?, ?> call(Supplier<Map<?, ?>> request) {
        try {
            Map<?, ?> response = request.get();
            if (response == null) {
                throw new PaymentException("Empty answer from PayPal");
            }
            return response;
        } catch (RestClientException e) {
            throw new PaymentException("PayPal request failed", e);
        }
    }
}
