package com.spring.henallux.hecathlon.dataAccess.repository;

import com.spring.henallux.hecathlon.dataAccess.entity.UserEntity;
import com.spring.henallux.hecathlon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    UserEntity findByEmail(String email);
}
