package com.poc.ecommerce.controller;

import java.util.List;

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
import com.poc.ecommerce.domain.user.UserRepository;
import com.poc.ecommerce.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final PlaceOrderCommandService placeOrderHandler;
    private final OrderQueryHandler orderQueryHandler;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public Order placeOrder(
            @RequestBody List<OrderItemRequest> items,
            HttpServletRequest request) {

    	
    	User user = extractUserInfoFromToken(request);
    	
        PlaceOrderCommand command =
                new PlaceOrderCommand(user.getId(), user.getRole(), items);

        return placeOrderHandler.handle(command);
    }
    
    @GetMapping("/user")
    public List<OrderView> getMyOrders(
    		HttpServletRequest request) {

    	
    	User userInfo = extractUserInfoFromToken(request);
    	
    	System.out.println("user >> " + userInfo);
    	
        return orderQueryHandler.handle(userInfo.getId());
    }
    
    
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new SecurityException("Missing or invalid Authorization header");
    }
    
//    private Long extractUserIdFromToken(HttpServletRequest request) {
//        String token = extractToken(request);
//        String username = jwtTokenProvider.getUsernameFromToken(token);
//        
//        // In real implementation, you'd fetch user ID from database
//        // For now, assuming username is the ID or you have a service to get ID
//        return Long.parseLong(username); // Or get from UserService
//    }
    
    
    @SuppressWarnings("deprecation")
	private User extractUserInfoFromToken(HttpServletRequest request) {
        String token = extractToken(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        // Fetch user details from database or token claims
        return userRepository.getReferenceById(userId);
    }
    
//    private void verifyAdminRole(HttpServletRequest request) {
//        User userInfo = extractUserInfoFromToken(request);
//        if (userInfo.getRole() != Role.ADMIN) {
//            throw new SecurityException("Admin role required");
//        }
//    }
    
    
}
