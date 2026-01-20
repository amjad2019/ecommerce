package com.poc.ecommerce.discount;

import java.math.BigDecimal;

public interface DiscountStrategy {
	BigDecimal apply(BigDecimal amount);
}