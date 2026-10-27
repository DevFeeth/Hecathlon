package com.spring.henallux.firstspringproject.dataAccess.util;

import com.spring.henallux.firstspringproject.dataAccess.entity.UserEntity;
import com.spring.henallux.firstspringproject.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = AuthorityMapper.class)
public interface UserMapper {
    UserEntity userModelToUserEntity(User user);
    User userEntityToUserModel(UserEntity userEntity);
}
