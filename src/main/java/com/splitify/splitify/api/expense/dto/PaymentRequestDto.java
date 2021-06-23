package com.splitify.splitify.api.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Calendar;

/** expense request dto */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {
  private Integer receivedBy;
  private Integer groupId;
  private Integer paidBy;
  private Integer createdBy;
  private BigDecimal amount;
  private Calendar onDate;
}
