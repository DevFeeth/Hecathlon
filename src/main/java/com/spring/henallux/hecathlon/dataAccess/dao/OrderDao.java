package com.spring.henallux.hecathlon.dataAccess.dao;

import com.spring.henallux.hecathlon.dataAccess.entity.OrderEntity;
import com.spring.henallux.hecathlon.dataAccess.repository.OrderRepository;
import com.spring.henallux.hecathlon.dataAccess.util.OrderMapper;
import com.spring.henallux.hecathlon.model.Order;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class OrderDao implements OrderDataAccess {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderDao(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public Order save(Order order) {
        OrderEntity saved = orderRepository.save(orderMapper.orderModelToOrderEntity(order));
        return orderMapper.orderEntityToOrderModel(saved);
    }

    @Override
    public Order findByIdAndCustomerEmail(Long id, String customerEmail) {
        OrderEntity entity = orderRepository.findByIdAndCustomerEmail(id, customerEmail);
        return entity != null ? orderMapper.orderEntityToOrderModel(entity) : null;
    }

    @Override
    public List<Order> findByCustomerEmail(String customerEmail) {
        return orderRepository.findByCustomerEmailOrderByDateDesc(customerEmail).stream()
                .map(orderMapper::orderEntityToOrderModel)
                .toList();
    }

}
