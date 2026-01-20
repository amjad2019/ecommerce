package com.poc.ecommerce.discount;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class PremiumDiscount implements DiscountStrategy {
	public BigDecimal apply(BigDecimal amount) {
		return amount.multiply(BigDecimal.valueOf(0.90));
	}
}