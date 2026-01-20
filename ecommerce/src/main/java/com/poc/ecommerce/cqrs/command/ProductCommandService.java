package com.poc.ecommerce.cqrs.command;

import com.poc.ecommerce.domain.product.Product;

public interface ProductCommandService {
    Product createProduct(CreateProductCommand command);
    Product updateProduct(Long id, CreateProductCommand command);
    void deleteProduct(Long id);
    Product restockProduct(Long id, Integer quantity);
}
