package com.assignment.Digitalwallet.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BuyProductRequest {
@NotNull(message="Product ID cannot be null")
@Positive(message="Product ID must be positive")
private Long product_id;
}
