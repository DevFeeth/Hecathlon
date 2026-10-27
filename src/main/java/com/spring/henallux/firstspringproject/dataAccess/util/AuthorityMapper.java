package com.spring.henallux.firstspringproject.dataAccess.util;

import com.spring.henallux.firstspringproject.dataAccess.entity.AuthorityEntity;
import com.spring.henallux.firstspringproject.model.Authority;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthorityMapper {

    @Mapping(source = "name", target = "name")
    Authority authorityEntityToAuthorityModel(AuthorityEntity authorityEntity);

    @Mapping(source = "name", target = "name")
    AuthorityEntity authorityModelToAuthorityEntity(Authority authority);

}
