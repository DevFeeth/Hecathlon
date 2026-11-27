package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.model.Product;
import com.spring.henallux.hecathlon.model.ProductSort;
import com.spring.henallux.hecathlon.model.Sport;
import com.spring.henallux.hecathlon.services.ProductService;
import com.spring.henallux.hecathlon.services.SportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Controller
@RequestMapping("/sports")
public class SportController {

    private final SportService sportService;
    private final ProductService productService;

    @Autowired
    public SportController(SportService sportService, ProductService productService) {
        this.sportService = sportService;
        this.productService = productService;
    }

    // the sports list itself comes from GlobalModelAttributes
    @GetMapping
    public String sports(Model model) {
        model.addAttribute("title", "Hecathlon");
        model.addAttribute("articleCounts", productService.countBySport());
        return "sports";
    }

    @GetMapping("/{sportId}")
    public String sport(Model model,
                        @PathVariable String sportId,
                        @RequestParam(name = "categorie", required = false) String category,
                        @RequestParam(defaultValue = "false") boolean promo,
                        @RequestParam(required = false) BigDecimal prixMax,
                        @RequestParam(defaultValue = "relevance") String tri,
                        @RequestParam(defaultValue = "1") int page) {
        Sport sport = sportService.findById(sportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        // values come from the URL: ignore the ones that make no sense instead of failing
        if (category != null && !sport.getCategories().contains(category)) {
            category = null;
        }
        if (prixMax != null && prixMax.signum() < 0) {
            prixMax = null;
        }
        ProductSort sort = ProductSort.fromParam(tri);

        Page<Product> products = productService.findBySport(sport.getId(), category, promo, prixMax, sort, page - 1);

        model.addAttribute("title", "Hecathlon");
        model.addAttribute("sport", sport);
        model.addAttribute("category", category);
        model.addAttribute("promo", promo);
        model.addAttribute("prixMax", prixMax);
        model.addAttribute("sort", sort);
        model.addAttribute("sorts", ProductSort.values());
        model.addAttribute("products", products);
        return "sport";
    }

}
