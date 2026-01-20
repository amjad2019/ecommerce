package com.poc.ecommerce.domain.product;

import org.springframework.stereotype.Service;

import com.poc.ecommerce.cqrs.query.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductWriteService {
	private final ProductRepository readRepo;

//	@Transactional
//	public Product create(CreateProductCommand cmd) {
//		Product p = repo.save(cmd.toEntity());
//		readRepo.save(new ProductReadModel(p.getId(), p.getName(), p.getPrice(), p.getQuantity() > 0));
//		return p;
//	}
}