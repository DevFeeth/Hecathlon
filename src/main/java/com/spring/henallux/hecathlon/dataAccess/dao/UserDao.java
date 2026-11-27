package com.spring.henallux.hecathlon.dataAccess.dao;

import com.spring.henallux.hecathlon.dataAccess.entity.UserEntity;
import com.spring.henallux.hecathlon.dataAccess.repository.UserRepository;
import com.spring.henallux.hecathlon.dataAccess.util.UserMapper;
import com.spring.henallux.hecathlon.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserDao implements UserDataAccess{
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserDao(UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user){
        UserEntity entity = userMapper.userModelToUserEntity(user);
        UserEntity savedEntity = userRepository.save(entity);
        return userMapper.userEntityToUserModel(savedEntity);
    }

    @Override
    public User findByEmail(String email) {
        UserEntity entity = userRepository.findByEmail(email);
        return entity != null ? userMapper.userEntityToUserModel(entity) : null;
    }

}
