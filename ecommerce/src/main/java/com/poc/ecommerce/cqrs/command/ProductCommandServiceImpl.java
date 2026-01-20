package com.poc.ecommerce.cqrs.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poc.ecommerce.cqrs.query.ProductQuery;
import com.poc.ecommerce.cqrs.query.ProductRepository;
import com.poc.ecommerce.domain.product.Product;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductCommandServiceImpl implements ProductCommandService {
    
    private final ProductRepository productRepository;
//    private final CustomRepository customRepository;
    
    @Override
    @Transactional
    public Product createProduct(CreateProductCommand command) {
        Product product = Product.builder()
                .name(command.getName())
                .description(command.getDescription())
                .price(command.getPrice())
                .quantity(command.getQuantity())
                .build();
        
        return productRepository.save(product);
    }
    
    @Override
    @Transactional
    public Product updateProduct(Long id, CreateProductCommand command) {
    	Product productObj = productRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        
//    	Product productObj = new Product(product.getId(), 
//    			product.getName(), 
//    			product.getDescription(), 
//    			product.getPrice(), 
//    			product.getQuantity(), 
//    			false, 
//    			null, 
//    			null);
    	
    	
    	
        if (command.getName() != null) {
        	productObj.setName(command.getName());
        }
        if (command.getDescription() != null) {
        	productObj.setDescription(command.getDescription());
        }
        if (command.getPrice() != null) {
        	productObj.setPrice(command.getPrice());
        }
        if (command.getQuantity() != null) {
        	productObj.setQuantity(command.getQuantity());
        }
        
        return productRepository.save(productObj);
    }
    
    @Override
    @Transactional
    public void deleteProduct(Long id) {
    	Product product = productRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        
//        customRepository.softDelete(product);
    }
    
    @Override
    @Transactional
    public Product restockProduct(Long id, Integer quantity) {
    	Product product = productRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        
//    	convertToProductQuery(product);
    	product.setQuantity(product.getQuantity() + quantity);
        return productRepository.save(product);
    }
    
//    private ProductQuery convertToProductQuery(Product product) {
//        if (product == null) {
//            return null;
//        }
//        
//        return new ProductQuery(
//                product.getId(),
//                product.getName(),
//                product.getDescription(),
//                product.getPrice(),
//                product.getQuantity(),
//                product.isDeleted(),
//                product.getCreatedAt(),
//                product.getUpdatedAt()
//        );
//    }
}
