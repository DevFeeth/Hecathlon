package com.spring.henallux.hecathlon.services;

import com.spring.henallux.hecathlon.dataAccess.dao.ProductDataAccess;
import com.spring.henallux.hecathlon.model.Product;
import com.spring.henallux.hecathlon.model.ProductSort;
import com.spring.henallux.hecathlon.model.ProductType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {
    public static final int PAGE_SIZE = 9;

    private final ProductDataAccess productDataAccess;

    @Autowired
    public ProductService(ProductDataAccess productDataAccess) {
        this.productDataAccess = productDataAccess;
    }

    public List<Product> getFeaturedProducts() {
        return productDataAccess.findFeatured();
    }

    public List<Product> getHighlightedProducts() {
        return productDataAccess.findHighlighted();
    }

    public Optional<Product> findById(String id) {
        return Optional.ofNullable(productDataAccess.findById(id));
    }

    public List<Product> findRelated(Product product, int max) {
        return productDataAccess.findOthersOfSport(product.getSportId(), List.of(product.getId()), max);
    }

    public List<Product> findSuggestions(String sportId, Collection<String> excludedIds, int max) {
        return productDataAccess.findOthersOfSport(sportId, excludedIds, max);
    }

    public Map<String, Long> countBySport() {
        return productDataAccess.countBySport();
    }

    public List<ProductType> findTypesBySport(String sportId) {
        return productDataAccess.findTypesOfSport(sportId);
    }

    public Page<Product> findBySport(String sportId, String typeId, boolean onSaleOnly, BigDecimal maxPrice,
                                     ProductSort sort, int page) {
        Page<Product> result = productDataAccess.search(sportId, typeId, onSaleOnly, maxPrice,
                PageRequest.of(Math.max(page, 0), PAGE_SIZE, sort.getSort()));
        if (result.getTotalPages() > 0 && result.getNumber() >= result.getTotalPages()) {
            result = productDataAccess.search(sportId, typeId, onSaleOnly, maxPrice,
                    PageRequest.of(result.getTotalPages() - 1, PAGE_SIZE, sort.getSort()));
        }
        return result;
    }
}
