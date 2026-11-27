package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.TestCatalog;
import com.spring.henallux.hecathlon.configuration.MainConfiguration;
import com.spring.henallux.hecathlon.configuration.WebSecurityConfiguration;
import com.spring.henallux.hecathlon.model.RegistrationForm;
import com.spring.henallux.hecathlon.services.AccountService;
import com.spring.henallux.hecathlon.services.SportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest({InscriptionController.class, LoginController.class})
@Import({WebSecurityConfiguration.class, MainConfiguration.class})
class InscriptionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private SportService sportService;

    @BeforeEach
    void setUp() {
        when(sportService.getSports()).thenReturn(TestCatalog.sports());
    }

    private static MockHttpServletRequestBuilder signUp(String... overrides) {
        Map<String, String> fields = new LinkedHashMap<>(Map.of(
                "firstName", "Alex", "lastName", "Martin", "email", "Alex@Example.com",
                "password", "Motdepasse1", "confirmPassword", "Motdepasse1", "phone", "0470 12 34 56",
                "address", "Rue de Fer 12", "postalCode", "5000", "city", "Namur", "country", "Belgique"));
        fields.put("acceptTerms", "true");
        for (int i = 0; i < overrides.length; i += 2) {
            fields.put(overrides[i], overrides[i + 1]);
        }
        MockHttpServletRequestBuilder request = post("/inscription").with(csrf());
        fields.forEach(request::param);
        return request;
    }

    @Test
    void validSignUpIsRegistered() throws Exception {
        mockMvc.perform(signUp("addressComplement", "", "newsletter", "true"))
                .andExpect(redirectedUrl("/login?registered"));

        ArgumentCaptor<RegistrationForm> form = ArgumentCaptor.forClass(RegistrationForm.class);
        verify(accountService).register(form.capture());
        assertEquals("alex@example.com", form.getValue().getEmail());
        assertEquals("0470123456", form.getValue().getPhone());
    }

    @Test
    void existingLoginIsRefused() throws Exception {
        when(accountService.isEmailTaken("alex@example.com")).thenReturn(true);

        mockMvc.perform(signUp())
                .andExpect(view().name("login"))
                .andExpect(model().attributeHasFieldErrorCode("registrationForm", "email", "email.taken"));
        verify(accountService, never()).register(any());
    }

    @Test
    void passwordMustBeTypedTwiceTheSame() throws Exception {
        mockMvc.perform(signUp("confirmPassword", "Autre1234"))
                .andExpect(model().attributeHasFieldErrorCode("registrationForm", "confirmPassword", "password.mismatch"));
        verify(accountService, never()).register(any());
    }

    @Test
    void numericFieldsOnlyAcceptDigitsWithTheRightLength() throws Exception {
        mockMvc.perform(signUp("phone", "04ab123456", "postalCode", "50A0"))
                .andExpect(model().attributeHasFieldErrorCode("registrationForm", "phone", "Pattern"))
                .andExpect(model().attributeHasFieldErrorCode("registrationForm", "postalCode", "Pattern"));
        mockMvc.perform(signUp("phone", "0470", "postalCode", "500000"))
                .andExpect(model().attributeHasFieldErrorCode("registrationForm", "phone", "Pattern"))
                .andExpect(model().attributeHasFieldErrorCode("registrationForm", "postalCode", "Pattern"));
        verify(accountService, never()).register(any());
    }

    @Test
    void emailNeedsAnAtAndADot() throws Exception {
        mockMvc.perform(signUp("email", "alex@example"))
                .andExpect(model().attributeHasFieldErrorCode("registrationForm", "email", "Pattern"));
    }

    @Test
    void requiredFieldsTermsAndPasswordRulesAreChecked() throws Exception {
        mockMvc.perform(signUp("lastName", "", "address", "", "city", "", "password", "faible", "confirmPassword", "faible", "acceptTerms", "false"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("registrationForm", "lastName", "address", "city", "password", "acceptTerms"));
        verify(accountService, never()).register(any());
    }

    @Test
    void loginPageShowsBothForms() throws Exception {
        mockMvc.perform(get("/inscription")).andExpect(redirectedUrl("/login#inscription"));
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Premier élan ? Rejoignez-nous.")))
                .andExpect(content().string(containsString("name=\"confirmPassword\"")))
                .andExpect(content().string(containsString("name=\"phone\"")))
                .andExpect(content().string(containsString("name=\"remember-me\"")));
    }
}
