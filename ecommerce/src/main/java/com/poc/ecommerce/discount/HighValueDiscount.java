package com.poc.ecommerce.discount;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.poc.ecommerce.domain.order.Order;

@Component
public class HighValueDiscount implements DiscountStrategy {

	    @Override
	    public BigDecimal apply(Order order, BigDecimal total) {
	        if (total.compareTo(BigDecimal.valueOf(500)) > 0) {
	            return total.multiply(BigDecimal.valueOf(0.95));
	        }
	        return total;
	    }
}