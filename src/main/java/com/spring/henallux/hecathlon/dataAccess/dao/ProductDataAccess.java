package com.spring.henallux.hecathlon.dataAccess.dao;

import com.spring.henallux.hecathlon.model.Product;
import com.spring.henallux.hecathlon.model.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ProductDataAccess {
    List<Product> findFeatured();

    List<Product> findHighlighted();

    Product findById(String id);

    Page<Product> search(String sportId, String typeId, boolean onSaleOnly, BigDecimal maxPrice, Pageable pageable);

    List<Product> findOthersOfSport(String sportId, Collection<String> excludedIds, int max);

    Map<String, Long> countBySport();

    List<ProductType> findTypesOfSport(String sportId);
}
