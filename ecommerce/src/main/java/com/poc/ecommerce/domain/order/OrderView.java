package com.poc.ecommerce.domain.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderView {
    private Long orderId;
    private BigDecimal orderTotal;
    private LocalDateTime createdAt;
}

