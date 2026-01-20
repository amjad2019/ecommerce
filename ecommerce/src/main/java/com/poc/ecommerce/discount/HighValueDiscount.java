package com.poc.ecommerce.discount;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class HighValueDiscount implements DiscountStrategy {
	public BigDecimal apply(BigDecimal amount) {
		return amount.compareTo(BigDecimal.valueOf(500)) > 0 ? amount.multiply(BigDecimal.valueOf(0.95)) : amount;
	}
}