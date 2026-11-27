package com.spring.henallux.hecathlon.services;

import com.spring.henallux.hecathlon.dataAccess.dao.UserDataAccess;
import com.spring.henallux.hecathlon.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {
    private final UserDataAccess userDAO;

    @Autowired
    public UserDetailsServiceImplementation( UserDataAccess userDAO){
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.findByEmail(username.trim().toLowerCase(Locale.ROOT));
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

}
