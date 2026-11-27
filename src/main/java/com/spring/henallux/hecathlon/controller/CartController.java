package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.constants.Constants;
import com.spring.henallux.hecathlon.model.Cart;
import com.spring.henallux.hecathlon.model.Product;
import com.spring.henallux.hecathlon.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/panier")
@SessionAttributes({Constants.CART})
public class CartController {
    private static final int SUGGESTIONS = 4;

    private final ProductService productService;

    @Autowired
    public CartController(ProductService productService) {
        this.productService = productService;
    }

    @ModelAttribute(Constants.CART)
    public Cart cart() {
        return new Cart();
    }

    @GetMapping
    public String cart(Model model, @ModelAttribute(Constants.CART) Cart cart) {
        List<Product> suggestions = cart.isEmpty()
                ? productService.getFeaturedProducts()
                : productService.findSuggestions(
                        cart.getLines().iterator().next().getProduct().getSportId(),
                        cart.getLines().stream().map(line -> line.getProduct().getId()).toList(),
                        SUGGESTIONS);
        model.addAttribute("title", "Hecathlon");
        model.addAttribute("suggestions", suggestions);
        return "cart";
    }

    @PostMapping("/ajouter")
    public String add(@ModelAttribute(Constants.CART) Cart cart,
                      @RequestParam String productId,
                      @RequestParam(required = false) String size,
                      @RequestParam(defaultValue = "1") int quantity) {
        Product product = productService.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (product.getSizes().isEmpty()) {
            size = null;
        } else if (!product.getSizes().contains(size)) {
            return "redirect:/produits/" + product.getId() + "?taille";
        }
        if (quantity < 1 || quantity > Cart.MAX_QUANTITY) {
            return "redirect:/produits/" + product.getId() + "?quantite";
        }
        cart.add(product, size, quantity);
        return "redirect:/panier";
    }

    @PostMapping("/quantite")
    public String quantity(@ModelAttribute(Constants.CART) Cart cart, @RequestParam String key, @RequestParam int quantity) {
        cart.setQuantity(key, quantity);
        return "redirect:/panier";
    }

    @PostMapping("/supprimer")
    public String remove(@ModelAttribute(Constants.CART) Cart cart, @RequestParam String key) {
        cart.remove(key);
        return "redirect:/panier";
    }

}
