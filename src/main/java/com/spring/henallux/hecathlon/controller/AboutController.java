package com.spring.henallux.hecathlon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {
    @GetMapping("/a-propos")
    public String about(Model model) {
        model.addAttribute("title", "Hecathlon");
        return "about";
    }
}
