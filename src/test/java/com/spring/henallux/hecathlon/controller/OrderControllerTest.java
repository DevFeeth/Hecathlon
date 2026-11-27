package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.TestCatalog;
import com.spring.henallux.hecathlon.configuration.MainConfiguration;
import com.spring.henallux.hecathlon.configuration.WebSecurityConfiguration;
import com.spring.henallux.hecathlon.constants.Constants;
import com.spring.henallux.hecathlon.dataAccess.dao.OrderDataAccess;
import com.spring.henallux.hecathlon.dataAccess.dao.UserDataAccess;
import com.spring.henallux.hecathlon.model.Cart;
import com.spring.henallux.hecathlon.model.Order;
import com.spring.henallux.hecathlon.model.OrderStatus;
import com.spring.henallux.hecathlon.model.User;
import com.spring.henallux.hecathlon.services.AccountService;
import com.spring.henallux.hecathlon.services.OrderService;
import com.spring.henallux.hecathlon.services.SportService;
import com.spring.henallux.hecathlon.services.payment.PaymentException;
import com.spring.henallux.hecathlon.services.payment.PaymentGateway;
import com.spring.henallux.hecathlon.services.payment.PaymentRedirect;
import com.spring.henallux.hecathlon.services.payment.PaymentResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import({WebSecurityConfiguration.class, MainConfiguration.class, OrderService.class, AccountService.class})
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderDataAccess orderDataAccess;

    @MockitoBean
    private UserDataAccess userDataAccess;

    @MockitoBean
    private PaymentGateway paymentGateway;

    @MockitoBean
    private SportService sportService;

    private User customer;
    private MockHttpSession session;
    private final AtomicReference<Order> stored = new AtomicReference<>();

    @BeforeEach
    void setUp() {
        customer = new User();
        customer.setEmail("alex@example.com");
        customer.setFirstName("Alex");
        customer.setLastName("Martin");
        customer.setPassword("x");
        when(userDataAccess.findByEmail("alex@example.com")).thenReturn(customer);
        when(sportService.getSports()).thenReturn(TestCatalog.sports());
        when(orderDataAccess.save(any())).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(7L);
            stored.set(order);
            return order;
        });
        when(orderDataAccess.findByIdAndCustomerEmail(7L, "alex@example.com")).thenAnswer(invocation -> stored.get());

        Cart cart = new Cart();
        cart.add(TestCatalog.pace500(), "42", 1);
        session = new MockHttpSession();
        session.setAttribute(Constants.CART, cart);
    }

    private MockHttpServletRequestBuilder homeDelivery(String... overrides) {
        Map<String, String> fields = new LinkedHashMap<>(Map.of(
                "firstName", "Alex", "lastName", "Martin", "email", "alex@example.com", "phone", "0470 12 34 56",
                "deliveryMode", "HOME", "address", "Rue de l'École 1", "postalCode", "5000",
                "city", "Namur", "country", "Belgique"));
        for (int i = 0; i < overrides.length; i += 2) {
            fields.put(overrides[i], overrides[i + 1]);
        }
        MockHttpServletRequestBuilder request = post("/commande/livraison").with(csrf()).with(user(customer)).session(session);
        fields.forEach(request::param);
        return request;
    }

    private void placeOrder() throws Exception {
        mockMvc.perform(homeDelivery()).andExpect(redirectedUrl("/commande/recapitulatif"));
        mockMvc.perform(post("/commande/recapitulatif").with(csrf()).with(user(customer)).session(session).param("acceptTerms", "true"))
                .andExpect(redirectedUrl("/commande/7/paiement"));
    }

    @Test
    void checkoutRequiresLogin() throws Exception {
        mockMvc.perform(get("/commande/livraison").session(session)).andExpect(redirectedUrl("/login"));
    }

    @Test
    void emptyCartGoesBackToTheCart() throws Exception {
        mockMvc.perform(get("/commande/livraison").with(user(customer))).andExpect(redirectedUrl("/panier"));
    }

    @Test
    void deliveryFormIsPrefilledWithTheSavedAddress() throws Exception {
        customer.setPhone("0470998877");
        customer.setAddress("Rue de Fer 12");
        customer.setPostalCode("5000");
        customer.setCity("Namur");
        customer.setCountry("Belgique");

        mockMvc.perform(get("/commande/livraison").with(user(customer)).session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("value=\"Rue de Fer 12\"")))
                .andExpect(content().string(containsString("value=\"0470998877\"")));
    }

    @Test
    void addressIsOnlyRequiredForHomeDelivery() throws Exception {
        mockMvc.perform(homeDelivery("address", "", "postalCode", "", "city", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("deliveryForm", "address", "postalCode", "city"));

        mockMvc.perform(homeDelivery("deliveryMode", "STORE", "address", "", "city", "", "postalCode", ""))
                .andExpect(redirectedUrl("/commande/recapitulatif"));
    }

    @Test
    void reviewNeedsTheDeliveryStepFirst() throws Exception {
        mockMvc.perform(get("/commande/recapitulatif").with(user(customer)).session(session))
                .andExpect(redirectedUrl("/commande/livraison"));
    }

    @Test
    void orderIsOnlySavedOnceConfirmedWithTheTerms() throws Exception {
        mockMvc.perform(homeDelivery());
        mockMvc.perform(get("/commande/recapitulatif").with(user(customer)).session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Confirmer ma commande (89,99 €)")));

        mockMvc.perform(post("/commande/recapitulatif").with(csrf()).with(user(customer)).session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("termsError", true));
        verify(orderDataAccess, never()).save(any());

        mockMvc.perform(post("/commande/recapitulatif").with(csrf()).with(user(customer)).session(session).param("acceptTerms", "true"))
                .andExpect(redirectedUrl("/commande/7/paiement"));

        Order order = stored.get();
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        assertEquals("alex@example.com", order.getCustomerEmail());
        assertEquals(new BigDecimal("89.99"), order.getTotal());
        assertNull(order.getPaidAt());
        assertNull(session.getAttribute(Constants.CART));
    }

    @Test
    void paymentGoesThroughPayPalAndConfirmsTheOrder() throws Exception {
        placeOrder();
        when(paymentGateway.createPayment(eq("HC000007"), eq(new BigDecimal("89.99")), anyString(), anyString()))
                .thenReturn(new PaymentRedirect("PAYPAL-1", "https://paypal.test/approve?token=PAYPAL-1"));
        when(paymentGateway.capture("PAYPAL-1")).thenReturn(new PaymentResult(true, new BigDecimal("89.99"), "EUR"));

        mockMvc.perform(get("/commande/7/paiement").with(user(customer)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Payer 89,99 € avec PayPal")));
        mockMvc.perform(post("/commande/7/paiement").with(csrf()).with(user(customer)))
                .andExpect(redirectedUrl("https://paypal.test/approve?token=PAYPAL-1"));
        verify(paymentGateway).createPayment(eq("HC000007"), eq(new BigDecimal("89.99")),
                eq("http://localhost/commande/7/paiement/retour"), eq("http://localhost/commande/7/paiement/annulation"));

        mockMvc.perform(get("/commande/7/paiement/retour").param("token", "PAYPAL-1").with(user(customer)))
                .andExpect(redirectedUrl("/commande/confirmation/7"));
        assertEquals(OrderStatus.CONFIRMED, stored.get().getStatus());
        assertNotNull(stored.get().getPaidAt());

        mockMvc.perform(get("/commande/confirmation/7").with(user(customer)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Merci Alex")));
    }

    @Test
    void forgedPaymentReturnIsRefused() throws Exception {
        placeOrder();
        stored.get().setPaymentReference("PAYPAL-1");

        mockMvc.perform(get("/commande/7/paiement/retour").param("token", "AUTRE").with(user(customer)))
                .andExpect(redirectedUrl("/commande/7/paiement?echec"));
        verify(paymentGateway, never()).capture(anyString());

        mockMvc.perform(get("/commande/confirmation/7").with(user(customer)))
                .andExpect(redirectedUrl("/commande/7/paiement"));
    }

    @Test
    void cancelledOrUnavailablePaymentKeepsTheOrder() throws Exception {
        placeOrder();
        when(paymentGateway.createPayment(anyString(), any(), anyString(), anyString())).thenThrow(new PaymentException("off"));

        mockMvc.perform(post("/commande/7/paiement").with(csrf()).with(user(customer)))
                .andExpect(redirectedUrl("/commande/7/paiement?indisponible"));
        mockMvc.perform(get("/commande/7/paiement/annulation").with(user(customer)))
                .andExpect(redirectedUrl("/commande/7/paiement?annule"));
        assertEquals(OrderStatus.PENDING_PAYMENT, stored.get().getStatus());
    }

    @Test
    void ordersOfOtherCustomersAreNotFound() throws Exception {
        mockMvc.perform(get("/commande/8/paiement").with(user(customer))).andExpect(status().isNotFound());
        mockMvc.perform(get("/commande/confirmation/8").with(user(customer))).andExpect(status().isNotFound());
    }
}
