package com.poc.ecommerce.cqrs.query;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poc.ecommerce.domain.product.Product;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        log.debug("Fetching product with id: {}", id);
        
        Product product = productRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        
        return product;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        log.debug("Fetching all products with pagination: {}", pageable);
        
        Page<Product> products = productRepository.findByDeletedIsFalse(pageable);
        
        return products;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String name, BigDecimal minPrice, 
                                           BigDecimal maxPrice, Boolean availableOnly, 
                                           Pageable pageable) {
        log.debug("Searching products with filters - name: {}, minPrice: {}, maxPrice: {}, availableOnly: {}",
                name, minPrice, maxPrice, availableOnly);
        
        // Validate price range
        validatePriceRange(minPrice, maxPrice);
        
        Page<Product> products = productRepository.searchProducts(
                name, minPrice, maxPrice, availableOnly, pageable);
        
        return products;
    }

    private void validatePriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum price cannot be negative");
        }
        
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maximum price cannot be negative");
        }
        
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
    }

    // Additional query methods for specific use cases

    @Transactional(readOnly = true)
    public Page<Product> getAvailableProducts(Pageable pageable) {
        log.debug("Fetching available products (quantity > 0)");
        
        Page<Product> products = productRepository.findByDeletedIsFalse(pageable);
        
        List<Product> activeProducts = products.getContent().stream()
                .filter(product -> product.getQuantity() > 0)
                .toList();
        
        return paginateList(activeProducts, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.debug("Fetching products by price range: {} to {}", minPrice, maxPrice);
        
        validatePriceRange(minPrice, maxPrice);
        
        Page<Product> products = productRepository.searchProducts(
                null, minPrice, maxPrice, null, pageable);
        
        return products;
    }

    @Transactional(readOnly = true)
    public Page<Product> searchProductsByName(String name, Pageable pageable) {
        log.debug("Searching products by name: {}", name);
        
        Page<Product> products = productRepository.searchProducts(
                name, null, null, null, pageable);
        
        return products;
    }

    @Transactional(readOnly = true)
    public boolean isProductAvailable(Long productId, Integer requestedQuantity) {
        log.debug("Checking availability for product id: {}, requested quantity: {}", 
                productId, requestedQuantity);
        
        if (requestedQuantity == null || requestedQuantity <= 0) {
            throw new IllegalArgumentException("Requested quantity must be positive");
        }
        
        Product product = productRepository.findByIdAndDeletedIsFalse(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        
        return product.getQuantity() >= requestedQuantity;
    }

    @Transactional(readOnly = true)
    public BigDecimal getProductPrice(Long productId) {
        log.debug("Fetching price for product id: {}", productId);
        
        Product product = productRepository.findByIdAndDeletedIsFalse(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        
        return product.getPrice();
    }

    @Transactional(readOnly = true)
    public Integer getProductStock(Long productId) {
        log.debug("Fetching stock for product id: {}", productId);
        
        Product product = productRepository.findByIdAndDeletedIsFalse(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        
        return product.getQuantity();
    }

    @Transactional(readOnly = true)
    public Page<Product> getInactiveProducts(Pageable pageable) {
        log.debug("Fetching inactive products");
        
        Page<Product > allProducts = productRepository.findByDeletedIsFalse(pageable);
        
        // Filter for inactive products
        Page<Product > inactiveProducts = allProducts;
        
        return inactiveProducts;
    }

    @Transactional(readOnly = true)
    public Page<Product> getLowStockProducts(Integer threshold, Pageable pageable) {
        log.debug("Fetching low stock products with threshold: {}", threshold);
        
        if (threshold == null || threshold < 0) {
            throw new IllegalArgumentException("Threshold must be a non-negative integer");
        }
        
        Page<Product> allProducts = productRepository.findByDeletedIsFalse(pageable);
        
        // Filter for low stock products
        List<Product> lowStockProducts = allProducts.filter(product -> 
        		product.getQuantity() <= threshold && product.getQuantity() > 0)
                .toList();
        
        return paginateList(lowStockProducts, pageable);
        
    }

    @Transactional(readOnly = true)
    public Page<Product> getOutOfStockProducts(Pageable pageable) {
        log.debug("Fetching out of stock products");
        
        Page<Product> allProducts = productRepository.findByDeletedIsFalse(pageable);
        
        // Filter for out of stock products
        List<Product> outOfStockProducts = allProducts.filter(product -> 
        product.getQuantity() == 0)
                .toList();
        
        return paginateList(outOfStockProducts, pageable);
        
    }

    @Transactional(readOnly = true)
    public long countActiveProducts() {
        log.debug("Counting active products");
        
        Page<Product> activeProducts = productRepository.findByDeletedIsFalse(Pageable.unpaged());
        
        return activeProducts.stream()
                .count();
    }

    @Transactional(readOnly = true)
    public long countAvailableProducts() {
        log.debug("Counting available products (quantity > 0)");
        
        Page<Product> activeProducts = productRepository.findByDeletedIsFalse(Pageable.unpaged());
        
        return activeProducts.stream()
                .filter(product -> product.getQuantity() > 0)
                .count();
    }

    // Cache support methods (could be enhanced with Spring Cache)
    @Transactional(readOnly = true)
    public Product getProductByIdWithCache(Long id) {
        // In a real implementation, you might use @Cacheable here
        return getProductById(id);
    }
    
    
    private Page<Product> paginateList(List<Product> products, Pageable pageable) {
        int totalElements = products.size();
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        
        List<Product> pageContent;
        
        if (totalElements < startItem) {
            pageContent = List.of();
        } else {
            int toIndex = Math.min(startItem + pageSize, totalElements);
            pageContent = products.subList(startItem, toIndex).stream()
                    .collect(Collectors.toList());
        }
        
        return new PageImpl<>(pageContent, PageRequest.of(currentPage, pageSize), totalElements);
    }
    
}