package com.splitify.splitify.api.expense.dto;

import com.splitify.splitify.api.security.dto.UserDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

/** expense details dto */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDetailsDto {
  private String expenseName;
  private Integer groupId;
  private UserDetailsDto paidBy;
  private UserDetailsDto createdBy;
  private BigDecimal amount;
  private BigDecimal settledAmount;
  private List<ExpenseShareDetailsDto> share;
  private Calendar onDate;
}
