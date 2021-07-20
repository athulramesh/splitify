package com.splitify.splitify.transaction.service;

import com.splitify.splitify.security.service.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDetails {
  private String expenseName;
  private Integer groupId;
  private UserDetails paidBy;
  private UserDetails createdBy;
  private BigDecimal amount;
  private BigDecimal settledAmount;
  private List<ExpenseShareDetails> share;
}
