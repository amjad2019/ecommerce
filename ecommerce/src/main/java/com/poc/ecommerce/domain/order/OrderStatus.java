package com.poc.ecommerce.domain.order;

public enum OrderStatus {
    PENDING,
    PROCESSING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED,
    FAILED
}
