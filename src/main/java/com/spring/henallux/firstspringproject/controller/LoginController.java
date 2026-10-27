package com.spring.henallux.firstspringproject.controller;

import com.spring.henallux.firstspringproject.configuration.WebSecurityConfiguration;
import com.spring.henallux.firstspringproject.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(WebSecurityConfiguration.LOGIN_PAGE_URL)
public class LoginController {

    @GetMapping
    public String login(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("mdp", new BCryptPasswordEncoder().encode("password"));
        return "login";
    }

}
