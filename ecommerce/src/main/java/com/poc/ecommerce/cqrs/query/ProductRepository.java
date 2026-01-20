package com.poc.ecommerce.cqrs.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poc.ecommerce.domain.product.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
//    Optional<ProductQuery> findByIdAndDeletedIsNull(Long id);
    
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.deleted = false")
    Optional<Product> findByIdAndDeletedIsFalse(@Param("id") Long id);
    
//    Page<ProductQuery> findByDeletedIsNull(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.deleted = false")
    Page<Product> findByDeletedIsFalse(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.deleted IS false AND " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:availableOnly IS NULL OR :availableOnly = false OR p.quantity > 0)")
    Page<Product> searchProducts(
            @Param("name") String name,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("availableOnly") Boolean availableOnly,
            Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.deleted IS false AND p.quantity > 0")
    List<Product> findAvailableProducts();
}
