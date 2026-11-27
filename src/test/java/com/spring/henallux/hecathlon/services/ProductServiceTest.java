package com.spring.henallux.hecathlon.services;

import com.spring.henallux.hecathlon.model.Product;
import com.spring.henallux.hecathlon.model.ProductSort;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductServiceTest {

    private final ProductService service = new ProductService();

    private Page<Product> find(String sport, String category, boolean promo, String maxPrice, ProductSort sort, int page) {
        return service.findBySport(sport, category, promo, maxPrice == null ? null : new BigDecimal(maxPrice), sort, page);
    }

    @Test
    void filtersBySportCategoryPromoAndMaxPrice() {
        assertTrue(find("running", null, false, null, ProductSort.RELEVANCE, 0).stream()
                .allMatch(p -> p.getSportId().equals("running")));
        assertTrue(find("football", "balls", false, null, ProductSort.RELEVANCE, 0).stream()
                .allMatch(p -> p.getCategory().equals("balls")));
        assertTrue(find("cycling", null, true, null, ProductSort.RELEVANCE, 0).stream()
                .allMatch(p -> p.getDiscountPercent() > 0));
        assertTrue(find("cycling", null, false, "79.99", ProductSort.RELEVANCE, 0).stream()
                .allMatch(p -> p.getPrice().compareTo(new BigDecimal("79.99")) <= 0));
    }

    @Test
    void sortsByPriceAndRating() {
        List<Product> asc = find("cycling", null, false, null, ProductSort.PRICE_ASC, 0).getContent();
        assertEquals(asc.stream().sorted(Comparator.comparing(Product::getPrice)).toList(), asc);

        List<Product> best = find("running", null, false, null, ProductSort.RATING, 0).getContent();
        assertEquals(4.8, best.getFirst().getRating());
    }

    @Test
    void relatedProductsAreFromTheSameSportWithoutTheProductItself() {
        Product pace = service.findById("pace-500").orElseThrow();
        List<Product> related = service.findRelated(pace, 4);
        assertEquals(4, related.size());
        assertTrue(related.stream().allMatch(p -> p.getSportId().equals("running") && !p.getId().equals("pace-500")));
        assertTrue(service.findById("inconnu").isEmpty());
    }

    @Test
    void pagesAreClampedToExistingOnes() {
        Page<Product> page = find("running", null, false, null, ProductSort.RELEVANCE, 42);
        assertEquals(0, page.getNumber());
        assertEquals(6, page.getTotalElements());

        Page<Product> none = find("running", null, false, "1", ProductSort.RELEVANCE, 0);
        assertTrue(none.isEmpty());
        assertEquals(0, none.getTotalElements());
    }

}
