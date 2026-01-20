package com.poc.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poc.ecommerce.cqrs.query.ProductReadModel;
import com.poc.ecommerce.cqrs.query.ProductReadRepository;
import com.poc.ecommerce.domain.product.Product;
import com.poc.ecommerce.domain.product.ProductRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
//@RequiredArgsConstructor
public class ProductQueryController {

//	private final ProductReadRepository repo1;
	
	private final ProductRepository repo;
	
	public ProductQueryController(ProductRepository repo) {
        this.repo = repo;
    }

	@GetMapping
	public Iterable<Product> findAll() {
		return repo.findAll();
	}
}
