package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.TestCatalog;
import com.spring.henallux.hecathlon.configuration.MainConfiguration;
import com.spring.henallux.hecathlon.configuration.WebSecurityConfiguration;
import com.spring.henallux.hecathlon.services.ProductService;
import com.spring.henallux.hecathlon.services.SportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import({WebSecurityConfiguration.class, MainConfiguration.class})
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SportService sportService;

    @MockitoBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        when(sportService.getSports()).thenReturn(TestCatalog.sports());
        when(sportService.findById("running")).thenReturn(Optional.of(TestCatalog.running()));
        when(sportService.findById("football")).thenReturn(Optional.of(TestCatalog.sports().get(1)));
        when(productService.findById("pace-500")).thenReturn(Optional.of(TestCatalog.pace500()));
        when(productService.findById("match-500")).thenReturn(Optional.of(TestCatalog.match500()));
        when(productService.findById("inconnu")).thenReturn(Optional.empty());
        when(productService.findRelated(any(), anyInt())).thenReturn(List.of(TestCatalog.air100()));
    }

    @Test
    void detailPageShowsEverythingAndAQuantityToAddToTheCart() throws Exception {
        mockMvc.perform(get("/produits/pace-500"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Chaussures running homme Pace 500")))
                .andExpect(content().string(containsString("89,99 €")))
                .andExpect(content().string(containsString("value=\"42\"")))
                .andExpect(content().string(containsString("name=\"quantity\"")))
                .andExpect(content().string(containsString("Chaussures")))
                .andExpect(content().string(containsString("T-shirt running homme Air 100")));
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
