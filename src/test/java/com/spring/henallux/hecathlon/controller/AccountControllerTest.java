package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.TestCatalog;
import com.spring.henallux.hecathlon.configuration.MainConfiguration;
import com.spring.henallux.hecathlon.configuration.WebSecurityConfiguration;
import com.spring.henallux.hecathlon.dataAccess.dao.UserDataAccess;
import com.spring.henallux.hecathlon.model.DeliveryMode;
import com.spring.henallux.hecathlon.model.Order;
import com.spring.henallux.hecathlon.model.OrderLine;
import com.spring.henallux.hecathlon.model.OrderStatus;
import com.spring.henallux.hecathlon.model.User;
import com.spring.henallux.hecathlon.services.AccountService;
import com.spring.henallux.hecathlon.services.OrderService;
import com.spring.henallux.hecathlon.services.SportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import({WebSecurityConfiguration.class, MainConfiguration.class, AccountService.class})
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserDataAccess userDataAccess;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private SportService sportService;

    private User customer;

    @BeforeEach
    void setUp() {
        customer = new User();
        customer.setEmail("alex@example.com");
        customer.setFirstName("Alex");
        customer.setLastName("Martin");
        customer.setPassword(passwordEncoder.encode("Ancien123"));
        when(userDataAccess.findByEmail("alex@example.com")).thenAnswer(invocation -> customer);
        when(userDataAccess.save(any())).thenAnswer(invocation -> customer = invocation.getArgument(0));
        when(orderService.findByCustomer("alex@example.com")).thenReturn(List.of());
        when(sportService.getSports()).thenReturn(TestCatalog.sports());
    }

    private static Order order(long id, OrderStatus status) {
        OrderLine line = new OrderLine();
        line.setProductId("pace-500");
        line.setProductName("Chaussures running homme Pace 500");
        line.setImage("/images/products/pace-500.jpg");
        line.setUnitPrice(new BigDecimal("89.99"));
        line.setQuantity(1);
        Order order = new Order();
        order.setId(id);
        order.setDate(LocalDateTime.of(2026, 11, 20, 10, 0));
        order.setStatus(status);
        order.setStatusDate(LocalDateTime.of(2026, 11, 21, 9, 0));
        order.setFirstName("Alex");
        order.setLastName("Martin");
        order.setPhone("0470123456");
        order.setDeliveryMode(DeliveryMode.HOME);
        order.setAddress("Rue 1");
        order.setPostalCode("5000");
        order.setCity("Namur");
        order.setCountry("Belgique");
        order.setSubtotal(new BigDecimal("89.99"));
        order.setDeliveryCost(BigDecimal.ZERO);
        order.setTotal(new BigDecimal("89.99"));
        order.setLines(List.of(line));
        return order;
    }

    @Test
    void accountRequiresLogin() throws Exception {
        mockMvc.perform(get("/compte")).andExpect(redirectedUrl("/login"));
    }

    @Test
    void everyPageOpensWithAPersonalMessageAndLogout() throws Exception {
        for (String page : List.of("/compte", "/compte/commandes", "/compte/informations", "/compte/adresses")) {
            mockMvc.perform(get(page).with(user(customer)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Bonjour Alex")))
                    .andExpect(content().string(containsString("action=\"/logout\"")));
        }
    }

    @Test
    void ordersCanBeFilteredByStatus() throws Exception {
        when(orderService.findByCustomer("alex@example.com"))
                .thenReturn(List.of(order(1, OrderStatus.CONFIRMED), order(2, OrderStatus.DELIVERED)));

        mockMvc.perform(get("/compte/commandes").with(user(customer)))
                .andExpect(content().string(containsString("HC000001")))
                .andExpect(content().string(containsString("HC000002")))
                .andExpect(content().string(containsString("En cours (1)")));

        mockMvc.perform(get("/compte/commandes").param("filtre", "en-cours").with(user(customer)))
                .andExpect(content().string(containsString("HC000001")))
                .andExpect(content().string(not(containsString("HC000002"))));
    }

    @Test
    void unpaidOrderOffersToPay() throws Exception {
        when(orderService.findByCustomer("alex@example.com")).thenReturn(List.of(order(3, OrderStatus.PENDING_PAYMENT)));

        mockMvc.perform(get("/compte/commandes").with(user(customer)))
                .andExpect(content().string(containsString("En attente de paiement")))
                .andExpect(content().string(containsString("href=\"/commande/3/paiement\"")));
    }

    @Test
    void orderDetailIsOnlyShownToItsCustomer() throws Exception {
        when(orderService.findForCustomer(1L, "alex@example.com")).thenReturn(Optional.of(order(1, OrderStatus.SHIPPED)));
        when(orderService.findForCustomer(2L, "alex@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/compte/commandes/1").with(user(customer)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Expédiée")))
                .andExpect(content().string(containsString("Dans votre colis")));
        mockMvc.perform(get("/compte/commandes/2").with(user(customer)))
                .andExpect(status().isNotFound());
    }

    @Test
    void profileIsUpdated() throws Exception {
        mockMvc.perform(post("/compte/informations").with(csrf()).with(user(customer))
                        .param("firstName", "Sam").param("lastName", "Martin").param("newsletter", "true"))
                .andExpect(redirectedUrl("/compte/informations?enregistre"));
        assertEquals("Sam", customer.getFirstName());
        assertTrue(customer.getNewsletter());
    }

    @Test
    void passwordChangeChecksTheCurrentOneAndTheConfirmation() throws Exception {
        mockMvc.perform(post("/compte/mot-de-passe").with(csrf()).with(user(customer))
                        .param("currentPassword", "Mauvais123").param("newPassword", "Nouveau123").param("confirmPassword", "Nouveau123"))
                .andExpect(model().attributeHasFieldErrorCode("passwordForm", "currentPassword", "password.wrong"));
        mockMvc.perform(post("/compte/mot-de-passe").with(csrf()).with(user(customer))
                        .param("currentPassword", "Ancien123").param("newPassword", "Nouveau123").param("confirmPassword", "Autre1234"))
                .andExpect(model().attributeHasFieldErrorCode("passwordForm", "confirmPassword", "password.mismatch"));

        mockMvc.perform(post("/compte/mot-de-passe").with(csrf()).with(user(customer))
                        .param("currentPassword", "Ancien123").param("newPassword", "Nouveau123").param("confirmPassword", "Nouveau123"))
                .andExpect(redirectedUrl("/compte/informations?motdepasse"));
        assertTrue(passwordEncoder.matches("Nouveau123", customer.getPassword()));
    }

    @Test
    void mainAddressIsValidatedAndSaved() throws Exception {
        mockMvc.perform(post("/compte/adresses").with(csrf()).with(user(customer))
                        .param("phone", "0470 12 34 56").param("address", "Rue de Fer 12").param("postalCode", "5A")
                        .param("city", "Namur").param("country", "Belgique"))
                .andExpect(model().attributeHasFieldErrors("addressForm", "postalCode"));

        mockMvc.perform(post("/compte/adresses").with(csrf()).with(user(customer))
                        .param("phone", "0470 12 34 56").param("address", "Rue de Fer 12").param("postalCode", "5000")
                        .param("city", "Namur").param("country", "Belgique"))
                .andExpect(redirectedUrl("/compte/adresses?enregistre"));
        assertEquals("Namur", customer.getCity());
        assertEquals("0470123456", customer.getPhone());
    }
}
