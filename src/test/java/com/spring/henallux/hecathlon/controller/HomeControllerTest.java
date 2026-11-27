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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HomeController.class)
@Import({WebSecurityConfiguration.class, MainConfiguration.class, SportService.class, ProductService.class})
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homePageIsPublicAndShowsTheThreeSports() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Course à pied")))
                .andExpect(content().string(containsString("Football")))
                .andExpect(content().string(containsString("Cyclisme")))
                .andExpect(content().string(containsString("89,99 €")))
                .andExpect(content().string(containsString("-18 %")));
    }

}
