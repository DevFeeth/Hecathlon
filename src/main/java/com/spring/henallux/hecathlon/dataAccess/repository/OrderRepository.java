package com.spring.henallux.hecathlon.dataAccess.repository;

import com.spring.henallux.hecathlon.dataAccess.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    OrderEntity findByIdAndCustomerEmail(Long id, String customerEmail);

    List<OrderEntity> findByCustomerEmailOrderByDateDesc(String customerEmail);
}
