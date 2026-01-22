package com.poc.ecommerce.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poc.ecommerce.cqrs.command.PlaceOrderCommand;
import com.poc.ecommerce.cqrs.command.PlaceOrderCommandService;
import com.poc.ecommerce.cqrs.query.OrderQueryHandler;
import com.poc.ecommerce.domain.order.Order;
import com.poc.ecommerce.domain.order.OrderItemRequest;
import com.poc.ecommerce.domain.order.OrderView;
import com.poc.ecommerce.domain.user.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final PlaceOrderCommandService placeOrderHandler;
    private final OrderQueryHandler orderQueryHandler;

    @PostMapping
    public Order placeOrder(
            @RequestBody List<OrderItemRequest> items,
            @AuthenticationPrincipal User user) {

        PlaceOrderCommand command =
                new PlaceOrderCommand(user.getId(), user.getRole(), items);

        return placeOrderHandler.handle(command);
    }
    
//    @PostMapping
//    @PreAuthorize("hasAnyRole('USER', 'PREMIUM_USER', 'ADMIN')")
//    @Operation(summary = "Create new order", description = "Place order with multiple products")
//    public ResponseEntity<Order> placeOrder(
//            @Valid @RequestBody List<OrderItemRequest> items, ) {
//        Long userId = getCurrentUserId(); // Extract from security context
//        OrderCommandResponse response = orderCommandService.createOrder(command, userId);
//        
//      PlaceOrderCommand command =
//      new PlaceOrderCommand(user.getId(), user.getRole(), items);
//        
//       return ResponseEntity.status(HttpStatus.CREATED).body(placeOrderHandler.handle(command));
//    }

    @GetMapping("/user")
    public List<OrderView> getMyOrders(
            @AuthenticationPrincipal User user) {

        return orderQueryHandler.handle(user.getId());
    }
}
