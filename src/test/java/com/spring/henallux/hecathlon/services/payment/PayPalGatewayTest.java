package com.spring.henallux.hecathlon.services.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PayPalGatewayTest {
    private MockRestServiceServer server;
    private PayPalGateway gateway;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://paypal.test");
        server = MockRestServiceServer.bindTo(builder).build();
        gateway = new PayPalGateway(builder, "client", "secret");
    }

    private void expectToken() {
        server.expect(requestTo("https://paypal.test/v1/oauth2/token"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Basic Y2xpZW50OnNlY3JldA=="))
                .andExpect(content().string("grant_type=client_credentials"))
                .andRespond(withSuccess("{\"access_token\":\"TOKEN\"}", MediaType.APPLICATION_JSON));
    }

    @Test
    void createPaymentSendsTheAmountAndReturnsTheApprovalLink() {
        expectToken();
        server.expect(requestTo("https://paypal.test/v2/checkout/orders"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer TOKEN"))
                .andExpect(jsonPath("$.intent").value("CAPTURE"))
                .andExpect(jsonPath("$.purchase_units[0].reference_id").value("HC000007"))
                .andExpect(jsonPath("$.purchase_units[0].amount.currency_code").value("EUR"))
                .andExpect(jsonPath("$.purchase_units[0].amount.value").value("54.97"))
                .andExpect(jsonPath("$.payment_source.paypal.experience_context.return_url").value("https://shop/retour"))
                .andRespond(withSuccess("""
                        {"id":"PAYPAL-1","status":"PAYER_ACTION_REQUIRED","links":[
                          {"href":"https://paypal.test/v2/checkout/orders/PAYPAL-1","rel":"self"},
                          {"href":"https://paypal.test/checkoutnow?token=PAYPAL-1","rel":"payer-action"}]}
                        """, MediaType.APPLICATION_JSON));

        PaymentRedirect redirect = gateway.createPayment("HC000007", new BigDecimal("54.97"), "https://shop/retour", "https://shop/annulation");

        assertEquals("PAYPAL-1", redirect.paymentId());
        assertEquals("https://paypal.test/checkoutnow?token=PAYPAL-1", redirect.approvalUrl());
        server.verify();
    }

    @Test
    void captureReadsStatusAndAmount() {
        expectToken();
        server.expect(requestTo("https://paypal.test/v2/checkout/orders/PAYPAL-1/capture"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"id":"PAYPAL-1","status":"COMPLETED","purchase_units":[{"payments":{"captures":[
                          {"id":"CAP-1","status":"COMPLETED","amount":{"currency_code":"EUR","value":"54.97"}}]}}]}
                        """, MediaType.APPLICATION_JSON));

        PaymentResult result = gateway.capture("PAYPAL-1");

        assertTrue(result.completed());
        assertEquals(new BigDecimal("54.97"), result.amount());
        assertEquals("EUR", result.currency());
    }

    @Test
    void pendingCaptureIsNotCompleted() {
        expectToken();
        server.expect(requestTo("https://paypal.test/v2/checkout/orders/PAYPAL-1/capture"))
                .andRespond(withSuccess("""
                        {"status":"COMPLETED","purchase_units":[{"payments":{"captures":[
                          {"status":"PENDING","amount":{"currency_code":"EUR","value":"54.97"}}]}}]}
                        """, MediaType.APPLICATION_JSON));

        assertFalse(gateway.capture("PAYPAL-1").completed());
    }

    @Test
    void payPalErrorBecomesAPaymentException() {
        expectToken();
        server.expect(requestTo("https://paypal.test/v2/checkout/orders/PAYPAL-1/capture"))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY));

        assertThrows(PaymentException.class, () -> gateway.capture("PAYPAL-1"));
    }

    @Test
    void missingCredentialsAreReportedWithoutCallingPayPal() {
        PayPalGateway unconfigured = new PayPalGateway(RestClient.builder().baseUrl("https://paypal.test"), "", "");

        assertThrows(PaymentException.class, () -> unconfigured.capture("PAYPAL-1"));
    }
}
