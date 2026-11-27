package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.model.Product;
import com.spring.henallux.hecathlon.services.ProductService;
import com.spring.henallux.hecathlon.services.SportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/produits")
public class ProductController {
    private static final int RELATED_PRODUCTS = 4;

    private final ProductService productService;
    private final SportService sportService;

    @Autowired
    public ProductController(ProductService productService, SportService sportService) {
        this.productService = productService;
        this.sportService = sportService;
    }

    @GetMapping("/{productId}")
    public String product(Model model, @PathVariable String productId) {
        Product product = productService.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("title", product.getName() + " | Hecathlon");
        model.addAttribute("product", product);
        model.addAttribute("sport", sportService.findById(product.getSportId()).orElseThrow());
        model.addAttribute("related", productService.findRelated(product, RELATED_PRODUCTS));
        return "product";
    }

}
