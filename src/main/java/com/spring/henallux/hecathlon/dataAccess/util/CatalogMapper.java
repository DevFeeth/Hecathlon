package com.spring.henallux.hecathlon.dataAccess.util;

import com.spring.henallux.hecathlon.dataAccess.entity.ProductEntity;
import com.spring.henallux.hecathlon.dataAccess.entity.ProductTypeEntity;
import com.spring.henallux.hecathlon.dataAccess.entity.SportEntity;
import com.spring.henallux.hecathlon.model.Product;
import com.spring.henallux.hecathlon.model.ProductType;
import com.spring.henallux.hecathlon.model.Sport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CatalogMapper {
    Sport sportEntityToSportModel(SportEntity sportEntity);

    ProductType productTypeEntityToProductTypeModel(ProductTypeEntity productTypeEntity);

    @Mapping(source = "sport.id", target = "sportId")
    Product productEntityToProductModel(ProductEntity productEntity);
}
