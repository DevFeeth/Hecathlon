package com.spring.henallux.hecathlon.services;

import com.spring.henallux.hecathlon.model.Product;
import com.spring.henallux.hecathlon.model.ProductSort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// shortcut: hardcoded catalogue filtered in memory, move to an entity/repository/DAO
// (Spring Data returns the same Page<Product>) when products are stored in the database
@Service
public class ProductService {

    public static final int PAGE_SIZE = 9;

    private static Product product(String id, String name, String brand, String sportId, String category, String description,
                                   double rating, int reviewCount, String price, String oldPrice, boolean bestSeller) {
        return new Product(id, name, brand, sportId, category, description, rating, reviewCount, new BigDecimal(price),
                oldPrice == null ? null : new BigDecimal(oldPrice), bestSeller, "/images/products/" + id + ".jpg");
    }

    private static final Product PACE_500 = product("pace-500", "Chaussures running homme Pace 500", "Élan Running", "running", "shoes",
            "Amorti confort · 3 couleurs", 4.7, 248, "89.99", "109.99", false);
    private static final Product STRIKE_900 = product("strike-900", "Chaussures de football Strike 900 FG", "Élan Football", "football", "shoes",
            "Terrain sec · 2 couleurs", 4.6, 186, "69.99", null, true);
    private static final Product MATCH_500 = product("match-500", "Ballon de football Match 500 taille 5", "Élan Football", "football", "balls",
            "Cousu machine · Taille 5", 4.8, 94, "24.99", "29.99", false);
    private static final Product AERO_700 = product("aero-700", "Casque vélo route Aero 700", "Élan Vélo", "cycling", "helmets",
            "Ventilé · 3 tailles", 4.6, 75, "79.99", "99.99", false);
    private static final Product RAIN_500 = product("rain-500", "Veste de pluie vélo Rain 500", "Élan Vélo", "cycling", "gear",
            "Imperméable · Respirante", 4.5, 112, "59.99", null, false);

    private final List<Product> products = List.of(
            // running
            PACE_500,
            product("flow-700", "Chaussures running femme Flow 700", "Élan Running", "running", "shoes",
                    "Légèreté · 2 couleurs", 4.8, 94, "99.99", "119.99", false),
            product("air-100", "T-shirt running homme Air 100", "Élan Running", "running", "clothing",
                    "Respirant · 5 couleurs", 4.6, 186, "19.99", null, true),
            product("light-500", "Short running homme Light 500", "Élan Running", "running", "clothing",
                    "Poche zippée · 2 couleurs", 4.5, 128, "24.99", null, false),
            product("bottle-500", "Gourde souple running 500 ml", "Élan Running", "running", "accessories",
                    "Sans BPA · 500 ml", 4.7, 146, "12.99", null, false),
            product("gps-900", "Montre GPS Connect 900", "Élan Connect", "running", "accessories",
                    "GPS intégré · Autonomie 20 h", 4.6, 76, "129.99", "149.99", false),
            // football
            STRIKE_900,
            product("agility-500", "Chaussures de football enfant Agility 500", "Élan Football", "football", "shoes",
                    "Terrain sec · Scratch", 4.5, 88, "34.99", "39.99", false),
            MATCH_500,
            product("training-100", "Ballon d'entraînement Training 100 taille 5", "Élan Football", "football", "balls",
                    "Résistant · Toutes surfaces", 4.4, 203, "9.99", null, false),
            product("jersey-500", "Maillot de football Club 500", "Élan Football", "football", "clothing",
                    "Respirant · 4 couleurs", 4.5, 67, "19.99", null, false),
            product("socks-100", "Chaussettes de football F100", "Élan Football", "football", "clothing",
                    "Renforcées · 6 couleurs", 4.6, 154, "6.99", null, false),
            // cycling
            product("strada-500", "Vélo de route Strada 500", "Élan Vélo", "cycling", "bikes",
                    "Cadre aluminium · 16 vitesses", 4.7, 58, "699.99", "799.99", false),
            product("urban-100", "Vélo de ville Urban 100", "Élan Vélo", "cycling", "bikes",
                    "Panier · Éclairage intégré", 4.5, 41, "329.99", null, false),
            AERO_700,
            product("kid-500", "Casque vélo enfant Kid 500", "Élan Vélo", "cycling", "helmets",
                    "Réglable · 2 tailles", 4.8, 132, "24.99", null, true),
            RAIN_500,
            product("gloves-500", "Gants vélo Road 500", "Élan Vélo", "cycling", "gear",
                    "Rembourrés · Mitaines", 4.4, 97, "14.99", "17.99", false)
    );

    private final List<Product> featuredProducts = List.of(PACE_500, STRIKE_900, MATCH_500, AERO_700);
    private final List<Product> highlightedProducts = List.of(PACE_500, RAIN_500);

    public List<Product> getFeaturedProducts() {
        return featuredProducts;
    }

    public List<Product> getHighlightedProducts() {
        return highlightedProducts;
    }

    /** Number of products per sport id (sports without products are absent). */
    public Map<String, Long> countBySport() {
        return products.stream().collect(Collectors.groupingBy(Product::getSportId, Collectors.counting()));
    }

    /**
     * Products of a sport, filtered, sorted and cut into pages of {@link #PAGE_SIZE}.
     *
     * @param category null for every category
     * @param maxPrice null for no limit
     * @param page     0-based, clamped to the existing pages
     */
    public Page<Product> findBySport(String sportId, String category, boolean onSaleOnly, BigDecimal maxPrice,
                                     ProductSort sort, int page) {
        List<Product> found = products.stream()
                .filter(p -> p.getSportId().equals(sportId))
                .filter(p -> category == null || p.getCategory().equals(category))
                .filter(p -> !onSaleOnly || p.getDiscountPercent() > 0)
                .filter(p -> maxPrice == null || p.getPrice().compareTo(maxPrice) <= 0)
                .sorted(sort.getComparator())
                .toList();

        int lastPage = Math.max(0, (found.size() - 1) / PAGE_SIZE);
        int current = Math.clamp(page, 0, lastPage);
        int from = current * PAGE_SIZE;
        List<Product> content = found.subList(from, Math.min(from + PAGE_SIZE, found.size()));
        return new PageImpl<>(content, PageRequest.of(current, PAGE_SIZE), found.size());
    }

}
