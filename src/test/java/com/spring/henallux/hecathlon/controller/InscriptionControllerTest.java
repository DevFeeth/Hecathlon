package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.configuration.MainConfiguration;
import com.spring.henallux.hecathlon.configuration.WebSecurityConfiguration;
import com.spring.henallux.hecathlon.dataAccess.dao.UserDataAccess;
import com.spring.henallux.hecathlon.model.User;
import com.spring.henallux.hecathlon.services.SportService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(InscriptionController.class)
@Import({WebSecurityConfiguration.class, MainConfiguration.class, SportService.class})
class InscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserDataAccess userDataAccess;

    private static MockHttpServletRequestBuilder signUp(String username) {
        return post("/inscription").with(csrf())
                .param("username", username)
                .param("password", "motdepasse123")
                .param("firstName", "Alex")
                .param("lastName", "Martin")
                .param("email", "alex@example.com");
    }

    @Test
    void validSignUpSavesHashedPasswordAndRedirectsToLogin() throws Exception {
        mockMvc.perform(signUp("alexm"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userDataAccess).save(saved.capture());
        assertTrue(passwordEncoder.matches("motdepasse123", saved.getValue().getPassword()));
    }

    @Test
    void existingUsernameIsRejectedWithoutSaving() throws Exception {
        when(userDataAccess.findByUsername("alexm")).thenReturn(new User());

        mockMvc.perform(signUp("alexm"))
                .andExpect(status().isOk())
                .andExpect(view().name("inscription"))
                .andExpect(model().attributeHasFieldErrorCode("userForm", "username", "username.taken"));

        verify(userDataAccess, never()).save(any());
    }

    @Test
    void invalidFormIsRejectedWithoutSaving() throws Exception {
        mockMvc.perform(signUp("ab").param("email", "pas-un-email"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "username", "email"));

        verify(userDataAccess, never()).save(any());
    }

}
