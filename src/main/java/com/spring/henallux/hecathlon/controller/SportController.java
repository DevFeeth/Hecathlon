package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sports")
public class SportController {

    private final ProductService productService;

    @Autowired
    public SportController(ProductService productService) {
        this.productService = productService;
    }

    // the sports list itself comes from GlobalModelAttributes
    @GetMapping
    public String sports(Model model) {
        model.addAttribute("title", "Hecathlon");
        model.addAttribute("articleCounts", productService.countBySport());
        return "sports";
    }

}
