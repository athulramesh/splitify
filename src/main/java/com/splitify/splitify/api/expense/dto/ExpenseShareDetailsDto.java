package com.splitify.splitify.api.expense.dto;

import com.splitify.splitify.security.service.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Calendar;
/** expense share details dto */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseShareDetailsDto {
  private UserDetails ownerId;
  private BigDecimal amount;
  private BigDecimal settledAmount;
  private BigDecimal remainingAmount;
  private Calendar paidDate;
}
