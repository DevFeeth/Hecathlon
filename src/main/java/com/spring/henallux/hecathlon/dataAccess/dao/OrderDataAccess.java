package com.spring.henallux.hecathlon.dataAccess.dao;

import com.spring.henallux.hecathlon.model.Order;

import java.util.List;

public interface OrderDataAccess {
    Order save(Order order);

    Order findByIdAndCustomerEmail(Long id, String customerEmail);

    List<Order> findByCustomerEmail(String customerEmail);
}
