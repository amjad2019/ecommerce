package com.poc.ecommerce.cqrs.command;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductCommand {
    
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    @NotNull(message = "Price is required")
    @Positive
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;
    
    @NotNull(message = "Quantity is required")
    @Min(0)
    private Integer quantity;
}
