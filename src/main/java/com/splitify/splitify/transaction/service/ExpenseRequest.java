package com.splitify.splitify.transaction.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRequest {
  private String expenseName;
  private Integer groupId;
  private Integer paidBy;
  private Integer createdBy;
  private BigDecimal amount;
  private BigDecimal dueAmount;
  private Calendar onDate;
  private List<ShareDetails> share;
  private Boolean isExcessPayment;
}
