package com.spring.henallux.hecathlon.dataAccess.repository;

import com.spring.henallux.hecathlon.dataAccess.entity.SportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SportRepository extends JpaRepository<SportEntity, String> {
    List<SportEntity> findAllByOrderByPosition();
}
