package com.poc.ecommerce.cqrs.command;

import com.poc.ecommerce.domain.order.Order;

public interface PlaceOrderCommandService {
	public Order handle(PlaceOrderCommand command);
}
