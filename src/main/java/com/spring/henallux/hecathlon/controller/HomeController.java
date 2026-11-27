package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final ProductService productService;

    @Autowired
    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Hecathlon");
        model.addAttribute("featuredProducts", productService.getFeaturedProducts());
        model.addAttribute("highlightedProducts", productService.getHighlightedProducts());
        return "home";
    }

}
