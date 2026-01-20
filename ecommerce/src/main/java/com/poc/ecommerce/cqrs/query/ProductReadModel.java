package com.poc.ecommerce.cqrs.query;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Product")
public class ProductReadModel {

	@Id
	private Long id;
	private String name;
	private BigDecimal price;
	private boolean available;
}
