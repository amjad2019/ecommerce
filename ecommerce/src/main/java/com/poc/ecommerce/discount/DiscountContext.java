package com.poc.ecommerce.discount;

import java.math.BigDecimal;
import java.util.List;

public class DiscountContext {

	private final List<DiscountStrategy> strategies;

	public DiscountContext(List<DiscountStrategy> strategies) {
		this.strategies = strategies;
	}

	public BigDecimal apply(BigDecimal amount) {
		BigDecimal result = amount;
		for (DiscountStrategy s : strategies) {
			result = s.apply(result);
		}
		return result;
	}
}