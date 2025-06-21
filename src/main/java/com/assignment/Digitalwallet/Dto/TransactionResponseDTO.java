package com.assignment.Digitalwallet.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionResponseDTO {
    private String kind;
    private BigDecimal amt;
    private BigDecimal updated_bal;
    private LocalDateTime timestamp;
}