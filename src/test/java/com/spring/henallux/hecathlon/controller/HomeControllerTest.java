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

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({HomeController.class, AboutController.class})
@Import({WebSecurityConfiguration.class, MainConfiguration.class})
class HomeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SportService sportService;

    @MockitoBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        when(sportService.getSports()).thenReturn(TestCatalog.sports());
        when(productService.getFeaturedProducts()).thenReturn(List.of(TestCatalog.pace500(), TestCatalog.match500()));
        when(productService.getHighlightedProducts()).thenReturn(List.of(TestCatalog.pace500()));
    }

    @Test
    void homePageShowsSportsFromTheDatabaseAndProducts() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Course à pied")))
                .andExpect(content().string(containsString("Cyclisme")))
                .andExpect(content().string(containsString("89,99 €")))
                .andExpect(content().string(containsString("-18 %")));
    }

    @Test
    void sportNamesFollowTheLanguage() throws Exception {
        mockMvc.perform(get("/").param("lang", "en"))
                .andExpect(content().string(containsString("Running")))
                .andExpect(content().string(containsString("Cycling")));
    }

    @Test
    void everyPageLinksLoginSignUpAboutCatalogueAndCart() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("href=\"/login\"")))
                .andExpect(content().string(containsString("href=\"/login#inscription\"")))
                .andExpect(content().string(containsString("href=\"/a-propos\"")))
                .andExpect(content().string(containsString("href=\"/sports\"")))
                .andExpect(content().string(containsString("href=\"/panier\"")))
                .andExpect(content().string(containsString("lang=en")));
    }

    @Test
    void aboutPageIsPublic() throws Exception {
        mockMvc.perform(get("/a-propos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Notre histoire")));
    }
}
