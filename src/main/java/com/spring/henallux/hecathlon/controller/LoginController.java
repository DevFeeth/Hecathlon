package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.configuration.WebSecurityConfiguration;
import com.spring.henallux.hecathlon.model.RegistrationForm;
import com.spring.henallux.hecathlon.model.ValidationRules;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(WebSecurityConfiguration.LOGIN_PAGE_URL)
public class LoginController {
    @GetMapping
    public String login(Model model) {
        model.addAttribute("title", "Hecathlon");
        model.addAttribute("registrationForm", new RegistrationForm());
        model.addAttribute("countries", ValidationRules.COUNTRIES);
        return "login";
    }
}
