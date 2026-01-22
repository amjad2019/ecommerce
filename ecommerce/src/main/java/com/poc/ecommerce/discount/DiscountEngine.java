package com.poc.ecommerce.discount;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.poc.ecommerce.domain.order.Order;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscountEngine {

    private final List<DiscountStrategy> strategies;

    public BigDecimal applyDiscounts(Order order, BigDecimal baseTotal) {
        BigDecimal total = baseTotal;
        for (DiscountStrategy strategy : strategies) {
            total = strategy.apply(order, total);
        }
        return total;
    }
}
