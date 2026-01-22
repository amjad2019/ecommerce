package com.poc.ecommerce.discount;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.poc.ecommerce.domain.order.Order;

@Component
public class NoDiscountStrategy implements DiscountStrategy {

	@Override
    public BigDecimal apply(Order order, BigDecimal total) {
        return total;
    }
}

