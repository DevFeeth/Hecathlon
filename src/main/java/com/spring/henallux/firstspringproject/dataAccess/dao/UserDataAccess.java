package com.spring.henallux.firstspringproject.dataAccess.dao;

import com.spring.henallux.firstspringproject.model.User;

import java.util.ArrayList;

public interface UserDataAccess {
    User save(User user);
    User findByUsername(String username);
}
