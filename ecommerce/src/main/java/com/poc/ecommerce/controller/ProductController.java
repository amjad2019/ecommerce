package com.poc.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poc.ecommerce.cqrs.command.CreateProductCommand;
import com.poc.ecommerce.cqrs.command.ProductCommandService;
import com.poc.ecommerce.cqrs.query.ProductQueryService;
import com.poc.ecommerce.domain.product.Product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductCommandService productCommandService;
    private final ProductQueryService productQueryService;
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody CreateProductCommand command) {
        productCommandService.createProduct(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
    	Product product = productQueryService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Product> products = productQueryService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean availableOnly,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<Product> products = productQueryService.searchProducts(
                name, minPrice, maxPrice, availableOnly, pageable);
        return ResponseEntity.ok(products);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody CreateProductCommand command) {
        productCommandService.updateProduct(id, command);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productCommandService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/restock")
    public ResponseEntity<Product> restockProduct(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        productCommandService.restockProduct(id, quantity);
        return ResponseEntity.ok().build();
    }
}