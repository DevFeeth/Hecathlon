package com.spring.henallux.hecathlon.dataAccess.dao;

import com.spring.henallux.hecathlon.model.User;

import java.util.ArrayList;

public interface UserDataAccess {
    User save(User user);
    User findByEmail(String email);
}
