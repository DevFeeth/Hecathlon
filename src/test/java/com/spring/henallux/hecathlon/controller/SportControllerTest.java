package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.configuration.MainConfiguration;
import com.spring.henallux.hecathlon.configuration.WebSecurityConfiguration;
import com.spring.henallux.hecathlon.services.ProductService;
import com.spring.henallux.hecathlon.services.SportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SportController.class)
@Import({WebSecurityConfiguration.class, MainConfiguration.class, SportService.class, ProductService.class})
class SportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sportsPageIsPublicAndShowsEachSportWithItsArticleCount() throws Exception {
        mockMvc.perform(get("/sports"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("3 sports à explorer")))
                .andExpect(content().string(containsString("Course à pied")))
                .andExpect(content().string(containsString("6 articles")));
    }

    @Test
    void sportPageListsItsProducts() throws Exception {
        mockMvc.perform(get("/sports/football"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tout le football")))
                .andExpect(content().string(containsString("Maillot de football Club 500")))
                .andExpect(content().string(not(containsString("Vélo de route"))));
    }

    @Test
    void sportPageAppliesCategoryAndPromoFilters() throws Exception {
        mockMvc.perform(get("/sports/running").param("categorie", "shoes").param("promo", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Chaussures running femme Flow 700")))
                .andExpect(content().string(not(containsString("T-shirt running"))));
    }

    @Test
    void unknownSportIsNotFound() throws Exception {
        mockMvc.perform(get("/sports/curling")).andExpect(status().isNotFound());
    }

    @Test
    void invalidParametersAreIgnored() throws Exception {
        mockMvc.perform(get("/sports/cycling").param("categorie", "inconnue").param("tri", "n'importe-quoi")
                        .param("prixMax", "-5").param("page", "99"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Vélo de ville Urban 100")));
    }

}
