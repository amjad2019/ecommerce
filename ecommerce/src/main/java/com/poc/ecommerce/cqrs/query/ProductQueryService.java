package com.poc.ecommerce.cqrs.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.poc.ecommerce.domain.product.Product;

import java.math.BigDecimal;

public interface ProductQueryService {
	Product getProductById(Long id);
    Page<Product> getAllProducts(Pageable pageable);
    Page<Product> searchProducts(
            String name, 
            BigDecimal minPrice, 
            BigDecimal maxPrice, 
            Boolean availableOnly, 
            Pageable pageable);
}
