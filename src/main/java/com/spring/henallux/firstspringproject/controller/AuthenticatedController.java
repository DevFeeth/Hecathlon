package com.spring.henallux.firstspringproject.controller;

import com.spring.henallux.firstspringproject.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticatedController {

    @GetMapping("/authenticated")
    public String authenticated(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        System.out.println("Utilisateur connecté : " + user.getUsername());
        return "authenticated";
    }

}
