package com.poc.ecommerce.discount;

import java.math.BigDecimal;

import com.poc.ecommerce.domain.order.Order;

public interface DiscountStrategy {
    BigDecimal apply(Order order, BigDecimal currentTotal);
}