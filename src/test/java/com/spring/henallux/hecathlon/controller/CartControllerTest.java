package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.configuration.MainConfiguration;
import com.spring.henallux.hecathlon.configuration.WebSecurityConfiguration;
import com.spring.henallux.hecathlon.constants.Constants;
import com.spring.henallux.hecathlon.model.Cart;
import com.spring.henallux.hecathlon.services.ProductService;
import com.spring.henallux.hecathlon.services.SportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@Import({WebSecurityConfiguration.class, MainConfiguration.class, SportService.class, ProductService.class})
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Cart cartOf(MockHttpSession session) {
        return (Cart) session.getAttribute(Constants.CART);
    }

    @Test
    void addThenShowCartWithTotalAndHeaderCount() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/panier/ajouter").with(csrf()).session(session)
                        .param("productId", "pace-500").param("size", "42"))
                .andExpect(redirectedUrl("/panier"));
        mockMvc.perform(post("/panier/ajouter").with(csrf()).session(session)
                        .param("productId", "match-500"))
                .andExpect(redirectedUrl("/panier"));

        assertEquals(2, cartOf(session).getItemCount());
        mockMvc.perform(get("/panier").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Mon panier · 2 articles")))
                .andExpect(content().string(containsString("Taille 42")))
                .andExpect(content().string(containsString("114,98 €"))) // 89.99 + 24.99, free delivery
                .andExpect(content().string(containsString("Panier (2)")));
    }

    @Test
    void sizeIsRequiredForProductsWithSizes() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/panier/ajouter").with(csrf()).session(session)
                        .param("productId", "pace-500").param("size", "99"))
                .andExpect(redirectedUrl("/produits/pace-500?taille"));
        assertTrue(cartOf(session).isEmpty());
    }

    @Test
    void unknownProductIsNotFound() throws Exception {
        mockMvc.perform(post("/panier/ajouter").with(csrf()).param("productId", "inconnu"))
                .andExpect(status().isNotFound());
    }

    @Test
    void quantityChangeAndRemoval() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/panier/ajouter").with(csrf()).session(session).param("productId", "match-500"));

        mockMvc.perform(post("/panier/quantite").with(csrf()).session(session).param("key", "match-500").param("quantity", "3"))
                .andExpect(redirectedUrl("/panier"));
        assertEquals(3, cartOf(session).getItemCount());

        mockMvc.perform(post("/panier/supprimer").with(csrf()).session(session).param("key", "match-500"))
                .andExpect(redirectedUrl("/panier"));
        assertTrue(cartOf(session).isEmpty());
    }

    @Test
    void emptyCartIsPublicAndSaysSo() throws Exception {
        mockMvc.perform(get("/panier"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Votre panier est vide.")));
    }

}
