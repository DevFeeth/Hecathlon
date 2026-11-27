package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.TestCatalog;
import com.spring.henallux.hecathlon.configuration.MainConfiguration;
import com.spring.henallux.hecathlon.configuration.WebSecurityConfiguration;
import com.spring.henallux.hecathlon.constants.Constants;
import com.spring.henallux.hecathlon.model.Cart;
import com.spring.henallux.hecathlon.services.ProductService;
import com.spring.henallux.hecathlon.services.SportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@Import({WebSecurityConfiguration.class, MainConfiguration.class})
class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SportService sportService;

    @MockitoBean
    private ProductService productService;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        when(sportService.getSports()).thenReturn(TestCatalog.sports());
        when(productService.findById("pace-500")).thenReturn(Optional.of(TestCatalog.pace500()));
        when(productService.findById("match-500")).thenReturn(Optional.of(TestCatalog.match500()));
        when(productService.findById("inconnu")).thenReturn(Optional.empty());
        when(productService.getFeaturedProducts()).thenReturn(List.of(TestCatalog.air100()));
        when(productService.findSuggestions(any(), any(), anyInt())).thenReturn(List.of(TestCatalog.air100()));
    }

    private Cart cart() {
        return (Cart) session.getAttribute(Constants.CART);
    }

    @Test
    void chosenQuantityIsAddedAndTheCartShowsUnitPriceQuantityAndTotal() throws Exception {
        mockMvc.perform(post("/panier/ajouter").with(csrf()).session(session)
                        .param("productId", "pace-500").param("size", "42").param("quantity", "2"))
                .andExpect(redirectedUrl("/panier"));
        mockMvc.perform(post("/panier/ajouter").with(csrf()).session(session).param("productId", "match-500"))
                .andExpect(redirectedUrl("/panier"));

        assertEquals(3, cart().getItemCount());
        mockMvc.perform(get("/panier").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Mon panier · 3 articles")))
                .andExpect(content().string(containsString("Prix unitaire : 89,99 €")))
                .andExpect(content().string(containsString("179,98 €")))
                .andExpect(content().string(containsString("204,97 €")))
                .andExpect(content().string(containsString("Panier (3)")));
    }

    @Test
    void quantityMustBeBetweenOneAndTen() throws Exception {
        for (String quantity : List.of("0", "-3", "11")) {
            mockMvc.perform(post("/panier/ajouter").with(csrf()).session(session)
                            .param("productId", "match-500").param("quantity", quantity))
                    .andExpect(redirectedUrl("/produits/match-500?quantite"));
        }
        assertTrue(cart().isEmpty());
    }

    @Test
    void sizeIsRequiredForProductsWithSizes() throws Exception {
        mockMvc.perform(post("/panier/ajouter").with(csrf()).session(session)
                        .param("productId", "pace-500").param("size", "99"))
                .andExpect(redirectedUrl("/produits/pace-500?taille"));
        assertTrue(cart().isEmpty());
    }

    @Test
    void unknownProductIsNotFound() throws Exception {
        mockMvc.perform(post("/panier/ajouter").with(csrf()).param("productId", "inconnu"))
                .andExpect(status().isNotFound());
    }

    @Test
    void quantityCanBeChangedAndALineRemoved() throws Exception {
        mockMvc.perform(post("/panier/ajouter").with(csrf()).session(session).param("productId", "match-500"));

        mockMvc.perform(post("/panier/quantite").with(csrf()).session(session).param("key", "match-500").param("quantity", "3"))
                .andExpect(redirectedUrl("/panier"));
        assertEquals(3, cart().getItemCount());

        mockMvc.perform(post("/panier/supprimer").with(csrf()).session(session).param("key", "match-500"))
                .andExpect(redirectedUrl("/panier"));
        assertTrue(cart().isEmpty());
    }

    @Test
    void emptyCartIsVisibleWithoutLogin() throws Exception {
        mockMvc.perform(get("/panier"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Votre panier est vide.")));
    }
}
