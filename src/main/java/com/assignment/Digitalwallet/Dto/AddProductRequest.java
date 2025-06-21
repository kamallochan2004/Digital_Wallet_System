package com.assignment.Digitalwallet.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddProductRequest {
    @NotBlank(message = "Product name cannot be empty")
    @Size(max = 255, message = "Product name too long")
    private String name;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Size(max = 1000, message = "Description too long")
    private String description;

}