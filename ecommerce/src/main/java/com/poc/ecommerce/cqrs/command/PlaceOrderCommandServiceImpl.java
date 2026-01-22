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
        List<OrderItem> orderItems = new ArrayList<OrderItem>();

        for (OrderItemRequest req : command.getItems()) {

            Product product = productRepo.findById(req.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getQuantity() < req.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for product: " + product.getName());
            }

            BigDecimal itemTotal =
                    product.getPrice().multiply(BigDecimal.valueOf(req.getQuantity()));

            orderItems.add(new OrderItem(product.getId(), null, product, req.getQuantity(), itemTotal, itemTotal, baseTotal, itemTotal));
            
            baseTotal = baseTotal.add(itemTotal);

            // inventory update
            product.setQuantity(product.getQuantity() - req.getQuantity());
            productRepo.save(product);
        }

        Order order = new Order();
        User usr = new User();
        usr.setId(command.getUserId());
        usr.setRole(command.getRole());
        order.setUser(usr);
        order.setOrderItems(orderItems);

        BigDecimal finalTotal =
                discountEngine.applyDiscounts(order, baseTotal);

        order.setOrderTotal(finalTotal);

        return orderRepo.save(order);
    }
}

