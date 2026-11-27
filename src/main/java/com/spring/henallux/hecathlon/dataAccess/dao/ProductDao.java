package com.spring.henallux.hecathlon.dataAccess.dao;

import com.spring.henallux.hecathlon.dataAccess.entity.ProductEntity;
import com.spring.henallux.hecathlon.dataAccess.repository.ProductRepository;
import com.spring.henallux.hecathlon.dataAccess.util.CatalogMapper;
import com.spring.henallux.hecathlon.model.Product;
import com.spring.henallux.hecathlon.model.ProductType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductDao implements ProductDataAccess {
    private final ProductRepository productRepository;
    private final CatalogMapper catalogMapper;

    @Autowired
    public ProductDao(ProductRepository productRepository, CatalogMapper catalogMapper) {
        this.productRepository = productRepository;
        this.catalogMapper = catalogMapper;
    }

    @Override
    public List<Product> findFeatured() {
        return toModels(productRepository.findByFeaturedTrueOrderByPosition());
    }

    @Override
    public List<Product> findHighlighted() {
        return toModels(productRepository.findByHighlightedTrueOrderByPosition());
    }

    @Override
    public Product findById(String id) {
        return productRepository.findById(id).map(catalogMapper::productEntityToProductModel).orElse(null);
    }

    @Override
    public Page<Product> search(String sportId, String typeId, boolean onSaleOnly, BigDecimal maxPrice, Pageable pageable) {
        Specification<ProductEntity> specification = (root, query, builder) -> builder.equal(root.get("sport").get("id"), sportId);
        if (typeId != null) {
            specification = specification.and((root, query, builder) -> builder.equal(root.get("type").get("id"), typeId));
        }
        if (onSaleOnly) {
            specification = specification.and((root, query, builder) -> builder.and(
                    builder.isNotNull(root.get("oldPrice")),
                    builder.greaterThan(root.get("oldPrice"), root.get("price"))));
        }
        if (maxPrice != null) {
            specification = specification.and((root, query, builder) -> builder.lessThanOrEqualTo(root.get("price"), maxPrice));
        }
        return productRepository.findAll(specification, pageable).map(catalogMapper::productEntityToProductModel);
    }

    @Override
    public List<Product> findOthersOfSport(String sportId, Collection<String> excludedIds, int max) {
        return toModels(productRepository.findBySportIdAndIdNotIn(sportId, excludedIds, PageRequest.of(0, max, Sort.by("position"))));
    }

    @Override
    public Map<String, Long> countBySport() {
        return productRepository.countBySport().stream()
                .collect(Collectors.toMap(row -> (String) row[0], row -> (Long) row[1]));
    }

    @Override
    public List<ProductType> findTypesOfSport(String sportId) {
        return productRepository.findTypesOfSport(sportId).stream()
                .map(catalogMapper::productTypeEntityToProductTypeModel)
                .toList();
    }

    private List<Product> toModels(List<ProductEntity> entities) {
        return entities.stream().map(catalogMapper::productEntityToProductModel).toList();
    }
}
