package com.poc.ecommerce.cqrs.command;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poc.ecommerce.cqrs.query.ProductRepository;
import com.poc.ecommerce.discount.DiscountEngine;
import com.poc.ecommerce.domain.order.Order;
import com.poc.ecommerce.domain.order.OrderItem;
import com.poc.ecommerce.domain.order.OrderItemRequest;
import com.poc.ecommerce.domain.order.OrderRepository;
import com.poc.ecommerce.domain.order.OrderStatus;
import com.poc.ecommerce.domain.product.Product;
import com.poc.ecommerce.domain.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceOrderCommandServiceImpl implements PlaceOrderCommandService{

    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final DiscountEngine discountEngine;   

    @Override
    @Transactional
    public Order handle(PlaceOrderCommand command) {

        BigDecimal baseTotal = BigDecimal.ZERO;
//        List<OrderItem> orderItems = new ArrayList<>();

        // Create order first
        Order order = new Order();
        User usr = new User();
        usr.setId(command.getUserId());
        usr.setRole(command.getRole());
        order.setUser(usr);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderTotal(BigDecimal.valueOf(1));
        order.setSubtotal(BigDecimal.valueOf(1));
        order.setOrderNumber("ORD-" + System.currentTimeMillis()); // Simple order number

        // Save order FIRST to get ID
        order = orderRepo.save(order);

        for (OrderItemRequest req : command.getItems()) {
            Product product = productRepo.findById(req.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getQuantity() < req.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for product: " + product.getName());
            }

            BigDecimal itemTotal = 
                    product.getPrice().multiply(BigDecimal.valueOf(req.getQuantity()));

            // FIX: Create OrderItem properly with the saved Order
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);           // Set the managed order with ID
            orderItem.setProduct(product);       // Set the managed product
            orderItem.setQuantity(req.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setItemTotal(itemTotal);
            orderItem.setTotalPrice(itemTotal);
            
            
            // Add to order's collection (cascade will save it)
            order.getOrderItems().add(orderItem);
            
            baseTotal = baseTotal.add(itemTotal);

            // Update inventory
            product.setQuantity(product.getQuantity() - req.getQuantity());
            productRepo.save(product);
        }

        // Set totals
        order.setSubtotal(baseTotal);
        
        BigDecimal finalTotal = discountEngine.applyDiscounts(order, baseTotal);
        order.setOrderTotal(baseTotal);
        order.setDiscountAmount(baseTotal.subtract(finalTotal));
        order.setSubtotal(finalTotal);

        // Save final order with items (cascade should save OrderItems)
        return orderRepo.save(order);
    }

//    @Override
//    @Transactional
//    public Order handle(PlaceOrderCommand command) {
//
//        BigDecimal baseTotal = BigDecimal.ZERO;
//        List<OrderItem> orderItems = new ArrayList<OrderItem>();
//
//        for (OrderItemRequest req : command.getItems()) {
//
//            Product product = productRepo.findById(req.getProductId())
//                    .orElseThrow(() -> new RuntimeException("Product not found"));
//
//            if (product.getQuantity() < req.getQuantity()) {
//                throw new RuntimeException(
//                        "Insufficient stock for product: " + product.getName());
//            }
//
//            BigDecimal itemTotal =
//                    product.getPrice().multiply(BigDecimal.valueOf(req.getQuantity()));
//
//            orderItems.add(new OrderItem(product.getId(), null, product, req.getQuantity(), itemTotal, itemTotal, baseTotal, itemTotal));
//            
//            baseTotal = baseTotal.add(itemTotal);
//
//            // inventory update
//            product.setQuantity(product.getQuantity() - req.getQuantity());
//            productRepo.save(product);
//        }
//
//        Order order = new Order();
//        User usr = new User();
//        usr.setId(command.getUserId());
//        usr.setRole(command.getRole());
//        order.setUser(usr);
//        order.setOrderItems(orderItems);
//
//        BigDecimal finalTotal =
//                discountEngine.applyDiscounts(order, baseTotal);
//
//        order.setOrderTotal(finalTotal);
//        
//        order.setStatus(OrderStatus.PENDING);
//        
//        order.setSubtotal(finalTotal);
//        
////        order.setOrderNumber(null);
//
//        return orderRepo.save(order);
//    }
}

