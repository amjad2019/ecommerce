package com.poc.ecommerce.cqrs.command;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
