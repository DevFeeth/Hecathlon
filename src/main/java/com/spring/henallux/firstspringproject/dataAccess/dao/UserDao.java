package com.spring.henallux.firstspringproject.dataAccess.dao;

import com.spring.henallux.firstspringproject.dataAccess.entity.UserEntity;
import com.spring.henallux.firstspringproject.dataAccess.repository.UserRepository;
import com.spring.henallux.firstspringproject.dataAccess.util.UserMapper;
import com.spring.henallux.firstspringproject.model.User;
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
    public User findByUsername(String username) {
        UserEntity entity = userRepository.findByUsername(username);
        return entity != null ? userMapper.userEntityToUserModel(entity) : null;
    }

}
