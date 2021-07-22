package com.splitify.splitify.api.expense.dto;

import com.splitify.splitify.api.security.dto.UserDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndividualTransactionDto {
  private UserDetailsDto person;
  private Boolean isToPay;
  private BigDecimal amount;
}
