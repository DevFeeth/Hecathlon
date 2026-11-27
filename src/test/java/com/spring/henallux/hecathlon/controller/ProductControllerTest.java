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

@WebMvcTest(ProductController.class)
@Import({WebSecurityConfiguration.class, MainConfiguration.class, SportService.class, ProductService.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void productPageIsPublicAndShowsPriceSizesAndRelatedProducts() throws Exception {
        mockMvc.perform(get("/produits/pace-500"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Chaussures running homme Pace 500")))
                .andExpect(content().string(containsString("89,99 €")))
                .andExpect(content().string(containsString("value=\"42\"")))
                .andExpect(content().string(containsString("Complétez votre équipement")))
                .andExpect(content().string(containsString("Chaussures running femme Flow 700")));
    }

    @Test
    void productWithoutSizesShowsOneSize() throws Exception {
        mockMvc.perform(get("/produits/match-500"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Taille unique")))
                .andExpect(content().string(not(containsString("name=\"size\""))));
    }

    @Test
    void unknownProductIsNotFound() throws Exception {
        mockMvc.perform(get("/produits/inconnu")).andExpect(status().isNotFound());
    }

}
