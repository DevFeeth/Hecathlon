package com.spring.henallux.hecathlon.dataAccess.util;

import com.spring.henallux.hecathlon.dataAccess.entity.OrderEntity;
import com.spring.henallux.hecathlon.dataAccess.entity.OrderLineEntity;
import com.spring.henallux.hecathlon.model.Order;
import com.spring.henallux.hecathlon.model.OrderLine;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderEntity orderModelToOrderEntity(Order order);
    Order orderEntityToOrderModel(OrderEntity orderEntity);

    OrderLineEntity orderLineModelToOrderLineEntity(OrderLine line);
    OrderLine orderLineEntityToOrderLineModel(OrderLineEntity lineEntity);
}
