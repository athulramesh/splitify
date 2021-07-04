package com.splitify.splitify.api.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
/** expense request dto */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequestDto {
  private String expenseName;
  private Integer groupId;
  private Integer paidBy;
  private Integer createdBy;
  private BigDecimal amount;
  private List<ShareDetailsDto> share;
  private Boolean isExcessPayment;
}
