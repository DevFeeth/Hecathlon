package com.spring.henallux.hecathlon.services;

import com.spring.henallux.hecathlon.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

// shortcut: hardcoded catalogue, move to an entity/repository/DAO when products are stored in the database
@Service
public class ProductService {

    private static final Product PACE_500 = new Product("pace-500", "Chaussures running homme Pace 500", "Élan Running",
            "running", "Amorti confort · 3 couleurs", 4.7, 248, new BigDecimal("89.99"), new BigDecimal("109.99"), false, null);

    private final List<Product> featuredProducts = List.of(
            PACE_500,
            new Product("strike-900", "Chaussures de football Strike 900 FG", "Élan Football",
                    "football", "Terrain sec · 2 couleurs", 4.6, 186, new BigDecimal("69.99"), null, true, null),
            new Product("match-500", "Ballon de football Match 500 taille 5", "Élan Football",
                    "football", "Cousu machine · Taille 5", 4.8, 94, new BigDecimal("24.99"), new BigDecimal("29.99"), false, null),
            new Product("aero-700", "Casque vélo route Aero 700", "Élan Vélo",
                    "cycling", "Ventilé · 3 tailles", 4.6, 75, new BigDecimal("79.99"), new BigDecimal("99.99"), false, null)
    );

    private final List<Product> highlightedProducts = List.of(
            PACE_500,
            new Product("rain-500", "Veste de pluie vélo Rain 500", "Élan Vélo",
                    "cycling", "Imperméable · Respirante", 4.5, 112, new BigDecimal("59.99"), null, false, null)
    );

    public List<Product> getFeaturedProducts() {
        return featuredProducts;
    }

    public List<Product> getHighlightedProducts() {
        return highlightedProducts;
    }

}
