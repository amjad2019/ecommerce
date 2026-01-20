//package com.poc.ecommerce.controller;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/orders")
//@RequiredArgsConstructor
//public class OrderController {
//    
//    private final OrderService orderService;
//    private final AuthService authService;
//    
//    // USER and PREMIUM_USER can place orders
//    @PostMapping
//    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
//        var user = authService.getCurrentUser();
//        return ResponseEntity.ok(orderService.createOrder(order, user.getId()));
//    }
//    
//    // Users can view their own orders
//    @GetMapping("/user/me")
//    public ResponseEntity<List<Order>> getMyOrders() {
//        var user = authService.getCurrentUser();
//        return ResponseEntity.ok(orderService.getOrdersByUserId(user.getId()));
//    }
//    
//    // Only ADMIN can view all orders
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping
//    public ResponseEntity<List<Order>> getAllOrders() {
//        return ResponseEntity.ok(orderService.getAllOrders());
//    }
//    
//    // Users can view their specific order
//    @GetMapping("/{id}")
//    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
//        var user = authService.getCurrentUser();
//        Order order = orderService.getOrderById(id);
//        
//        // Users can only view their own orders unless they're ADMIN
//        if (!user.getRole().equals(Role.ADMIN) && !order.getUserId().equals(user.getId())) {
//            throw new RuntimeException("Access denied");
//        }
//        
//        return ResponseEntity.ok(order);
//    }
//}