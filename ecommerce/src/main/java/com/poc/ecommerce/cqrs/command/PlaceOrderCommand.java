package com.poc.ecommerce.cqrs.command;

import java.util.List;

import com.poc.ecommerce.domain.order.OrderItemRequest;
import com.poc.ecommerce.domain.user.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceOrderCommand {
    private Long userId;
    private Role role;
    private List<OrderItemRequest> items;
}

