package com.spring.henallux.firstspringproject.dataAccess.repository;

import com.spring.henallux.firstspringproject.dataAccess.entity.MagicKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MagicKeyRepository extends JpaRepository<MagicKeyEntity, String> {
}
