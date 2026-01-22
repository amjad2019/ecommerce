package com.poc.ecommerce.cqrs.query;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poc.ecommerce.domain.order.OrderRepository;
import com.poc.ecommerce.domain.order.OrderView;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryHandler {

    private final OrderRepository orderRepository;

    public List<OrderView> handle(Long userId) {
        return orderRepository.findUserOrders(userId);
    }
}

