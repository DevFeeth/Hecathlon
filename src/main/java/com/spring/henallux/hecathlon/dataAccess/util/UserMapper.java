package com.spring.henallux.hecathlon.dataAccess.util;

import com.spring.henallux.hecathlon.dataAccess.entity.UserEntity;
import com.spring.henallux.hecathlon.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = AuthorityMapper.class)
public interface UserMapper {
    UserEntity userModelToUserEntity(User user);
    User userEntityToUserModel(UserEntity userEntity);
}
