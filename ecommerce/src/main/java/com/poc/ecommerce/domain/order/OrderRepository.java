package com.poc.ecommerce.domain.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
   
	
	@Query("""
		    SELECT o
		    FROM Order o
		    JOIN FETCH o.orderItems
		    WHERE o.id = :orderId AND o.user.id = :userId
		""")
		Optional<Order> findOrderDetails(
		        @Param("orderId") Long orderId,
		        @Param("userId") Long userId);
	
	
	@Query("""
	        SELECT new com.poc.ecommerce.domain.order.OrderView(
	            o.id,
	            o.orderTotal,
	            o.createdAt
	        )
	        FROM Order o
	        WHERE o.user.id = :userId
	        ORDER BY o.createdAt DESC
	    """)
	    List<OrderView> findUserOrders(@Param("userId") Long userId);

}
