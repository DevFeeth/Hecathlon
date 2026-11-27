package com.spring.henallux.hecathlon.services;

import com.spring.henallux.hecathlon.model.Product;
import com.spring.henallux.hecathlon.model.ProductSort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// shortcut: hardcoded catalogue filtered in memory, move to an entity/repository/DAO
// (Spring Data returns the same Page<Product>) when products are stored in the database
@Service
public class ProductService {

    public static final int PAGE_SIZE = 9;

    private static final List<String> SHOE_SIZES = List.of("39", "40", "41", "42", "43", "44", "45", "46");
    private static final List<String> KID_SHOE_SIZES = List.of("30", "31", "32", "33", "34", "35", "36", "37", "38");
    private static final List<String> CLOTHING_SIZES = List.of("XS", "S", "M", "L", "XL");
    private static final List<String> SOCK_SIZES = List.of("35-38", "39-42", "43-46");
    private static final List<String> SML_SIZES = List.of("S", "M", "L");
    private static final List<String> ONE_SIZE = List.of();

    private static Product product(String id, String name, String brand, String sportId, String category,
                                   String description, String longDescription, double rating, int reviewCount,
                                   String price, String oldPrice, boolean bestSeller, List<String> sizes) {
        return new Product(id, name, brand, sportId, category, description, longDescription, rating, reviewCount,
                new BigDecimal(price), oldPrice == null ? null : new BigDecimal(oldPrice), bestSeller,
                "/images/products/" + id + ".jpg", sizes);
    }

    private static final Product PACE_500 = product("pace-500", "Chaussures running homme Pace 500", "Élan Running", "running", "shoes",
            "Amorti confort · 3 couleurs",
            "Votre alliée pour les sorties régulières sur route. Un amorti généreux, une foulée fluide et un maintien rassurant, du premier au dernier kilomètre.",
            4.7, 248, "89.99", "109.99", false, SHOE_SIZES);
    private static final Product STRIKE_900 = product("strike-900", "Chaussures de football Strike 900 FG", "Élan Football", "football", "shoes",
            "Terrain sec · 2 couleurs",
            "Une chaussure de précision pour terrains secs, avec une tige fine pour un toucher de balle optimal.",
            4.6, 186, "69.99", null, true, SHOE_SIZES);
    private static final Product MATCH_500 = product("match-500", "Ballon de football Match 500 taille 5", "Élan Football", "football", "balls",
            "Cousu machine · Taille 5",
            "Un ballon cousu machine, rond et stable, pour les matchs comme pour les entraînements.",
            4.8, 94, "24.99", "29.99", false, ONE_SIZE);
    private static final Product AERO_700 = product("aero-700", "Casque vélo route Aero 700", "Élan Vélo", "cycling", "helmets",
            "Ventilé · 3 tailles",
            "Un casque route léger et bien ventilé, avec molette de réglage pour un maintien précis.",
            4.6, 75, "79.99", "99.99", false, SML_SIZES);
    private static final Product RAIN_500 = product("rain-500", "Veste de pluie vélo Rain 500", "Élan Vélo", "cycling", "gear",
            "Imperméable · Respirante",
            "Une veste imperméable et respirante qui se range dans sa poche, pour ne jamais être surpris par la pluie.",
            4.5, 112, "59.99", null, false, CLOTHING_SIZES);

    private final List<Product> products = List.of(
            // running
            PACE_500,
            product("flow-700", "Chaussures running femme Flow 700", "Élan Running", "running", "shoes",
                    "Légèreté · 2 couleurs",
                    "Légère et dynamique, la Flow 700 accompagne les coureuses qui veulent gagner en vitesse sans sacrifier le confort.",
                    4.8, 94, "99.99", "119.99", false, SHOE_SIZES),
            product("air-100", "T-shirt running homme Air 100", "Élan Running", "running", "clothing",
                    "Respirant · 5 couleurs",
                    "Un t-shirt léger qui évacue la transpiration pour rester au sec, même quand le rythme s'accélère.",
                    4.6, 186, "19.99", null, true, CLOTHING_SIZES),
            product("light-500", "Short running homme Light 500", "Élan Running", "running", "clothing",
                    "Poche zippée · 2 couleurs",
                    "Un short léger avec slip intégré et poche zippée pour garder vos clés à portée de main.",
                    4.5, 128, "24.99", null, false, CLOTHING_SIZES),
            product("bottle-500", "Gourde souple running 500 ml", "Élan Running", "running", "accessories",
                    "Sans BPA · 500 ml",
                    "Une gourde souple qui se compresse à mesure que vous buvez : aucun ballottement pendant la course.",
                    4.7, 146, "12.99", null, false, ONE_SIZE),
            product("gps-900", "Montre GPS Connect 900", "Élan Connect", "running", "accessories",
                    "GPS intégré · Autonomie 20 h",
                    "Allure, distance, fréquence cardiaque : suivez chaque séance et progressez grâce au GPS intégré.",
                    4.6, 76, "129.99", "149.99", false, ONE_SIZE),
            // football
            STRIKE_900,
            product("agility-500", "Chaussures de football enfant Agility 500", "Élan Football", "football", "shoes",
                    "Terrain sec · Scratch",
                    "Une chaussure facile à enfiler grâce à son scratch, pensée pour les jeunes joueurs sur terrain sec.",
                    4.5, 88, "34.99", "39.99", false, KID_SHOE_SIZES),
            MATCH_500,
            product("training-100", "Ballon d'entraînement Training 100 taille 5", "Élan Football", "football", "balls",
                    "Résistant · Toutes surfaces",
                    "Un ballon résistant pour enchaîner les séances d'entraînement sur toutes les surfaces.",
                    4.4, 203, "9.99", null, false, ONE_SIZE),
            product("jersey-500", "Maillot de football Club 500", "Élan Football", "football", "clothing",
                    "Respirant · 4 couleurs",
                    "Un maillot respirant et léger, idéal pour les matchs et les entraînements en club.",
                    4.5, 67, "19.99", null, false, CLOTHING_SIZES),
            product("socks-100", "Chaussettes de football F100", "Élan Football", "football", "clothing",
                    "Renforcées · 6 couleurs",
                    "Des chaussettes montantes renforcées aux zones d'usure, compatibles avec les protège-tibias.",
                    4.6, 154, "6.99", null, false, SOCK_SIZES),
            // cycling
            product("strada-500", "Vélo de route Strada 500", "Élan Vélo", "cycling", "bikes",
                    "Cadre aluminium · 16 vitesses",
                    "Un vélo de route en aluminium, léger et polyvalent, pour vos premières longues sorties.",
                    4.7, 58, "699.99", "799.99", false, SML_SIZES),
            product("urban-100", "Vélo de ville Urban 100", "Élan Vélo", "cycling", "bikes",
                    "Panier · Éclairage intégré",
                    "Un vélo de ville confortable avec panier et éclairage intégré pour vos trajets du quotidien.",
                    4.5, 41, "329.99", null, false, SML_SIZES),
            AERO_700,
            product("kid-500", "Casque vélo enfant Kid 500", "Élan Vélo", "cycling", "helmets",
                    "Réglable · 2 tailles",
                    "Un casque enfant réglable et bien visible pour rouler en sécurité dès les premiers tours de roue.",
                    4.8, 132, "24.99", null, true, List.of("XS", "S")),
            RAIN_500,
            product("gloves-500", "Gants vélo Road 500", "Élan Vélo", "cycling", "gear",
                    "Rembourrés · Mitaines",
                    "Des mitaines rembourrées qui absorbent les vibrations et protègent vos mains en cas de chute.",
                    4.4, 97, "14.99", "17.99", false, List.of("S", "M", "L", "XL"))
    );

    private final List<Product> featuredProducts = List.of(PACE_500, STRIKE_900, MATCH_500, AERO_700);
    private final List<Product> highlightedProducts = List.of(PACE_500, RAIN_500);

    public List<Product> getFeaturedProducts() {
        return featuredProducts;
    }

    public List<Product> getHighlightedProducts() {
        return highlightedProducts;
    }

    public Optional<Product> findById(String id) {
        return products.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    /** Other products of the same sport, for "Complete your gear". */
    public List<Product> findRelated(Product product, int max) {
        return products.stream()
                .filter(p -> p.getSportId().equals(product.getSportId()) && !p.getId().equals(product.getId()))
                .limit(max)
                .toList();
    }

    /** Products of a sport that are not already in the cart, for the cart page. */
    public List<Product> findSuggestions(String sportId, Collection<String> excludedIds, int max) {
        return products.stream()
                .filter(p -> p.getSportId().equals(sportId) && !excludedIds.contains(p.getId()))
                .limit(max)
                .toList();
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
