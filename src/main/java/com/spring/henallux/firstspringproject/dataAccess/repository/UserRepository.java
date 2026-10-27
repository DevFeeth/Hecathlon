package com.spring.henallux.firstspringproject.dataAccess.repository;

import com.spring.henallux.firstspringproject.dataAccess.entity.UserEntity;
import com.spring.henallux.firstspringproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    UserEntity findByUsername(String username);
}
