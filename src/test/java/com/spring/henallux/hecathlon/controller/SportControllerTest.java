package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.TestCatalog;
import com.spring.henallux.hecathlon.configuration.MainConfiguration;
import com.spring.henallux.hecathlon.configuration.WebSecurityConfiguration;
import com.spring.henallux.hecathlon.model.ProductSort;
import com.spring.henallux.hecathlon.services.ProductService;
import com.spring.henallux.hecathlon.services.SportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SportController.class)
@Import({WebSecurityConfiguration.class, MainConfiguration.class})
class SportControllerTest {
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
        when(sportService.findById("curling")).thenReturn(Optional.empty());
        when(productService.findTypesBySport(any())).thenReturn(List.of(TestCatalog.SHOES, TestCatalog.CLOTHING));
        when(productService.countBySport()).thenReturn(Map.of("running", 2L));
        when(productService.findBySport(any(), any(), anyBoolean(), any(), any(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(TestCatalog.pace500(), TestCatalog.air100()), PageRequest.of(0, 9), 2));
    }

    @Test
    void categoriesPageListsSportsWithTheirTypesAndCounts() throws Exception {
        mockMvc.perform(get("/sports"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("3 sports à explorer")))
                .andExpect(content().string(containsString("Chaussures")))
                .andExpect(content().string(containsString("2 articles")));
    }

    @Test
    void productListShowsLabelPriceAndDetailLink() throws Exception {
        mockMvc.perform(get("/sports/running"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Chaussures running homme Pace 500")))
                .andExpect(content().string(containsString("89,99 €")))
                .andExpect(content().string(containsString("href=\"/produits/pace-500\"")));
    }

    @Test
    void validFiltersAreSentToTheService() throws Exception {
        mockMvc.perform(get("/sports/running").param("categorie", "shoes").param("promo", "true").param("tri", "price_asc"))
                .andExpect(status().isOk());

        verify(productService).findBySport("running", "shoes", true, null, ProductSort.PRICE_ASC, 0);
    }

    @Test
    void invalidParametersAreIgnored() throws Exception {
        mockMvc.perform(get("/sports/running").param("categorie", "inconnue").param("tri", "n'importe-quoi")
                        .param("prixMax", "-5").param("page", "99"))
                .andExpect(status().isOk());

        verify(productService).findBySport("running", null, false, null, ProductSort.RELEVANCE, 98);
    }

    @Test
    void unknownSportIsNotFound() throws Exception {
        mockMvc.perform(get("/sports/curling")).andExpect(status().isNotFound());
    }
}
