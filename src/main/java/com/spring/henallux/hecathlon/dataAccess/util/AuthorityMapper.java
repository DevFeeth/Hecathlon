package com.spring.henallux.hecathlon.dataAccess.util;

import com.spring.henallux.hecathlon.dataAccess.entity.AuthorityEntity;
import com.spring.henallux.hecathlon.model.Authority;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthorityMapper {

    @Mapping(source = "name", target = "name")
    Authority authorityEntityToAuthorityModel(AuthorityEntity authorityEntity);

    @Mapping(source = "name", target = "name")
    AuthorityEntity authorityModelToAuthorityEntity(Authority authority);

}
