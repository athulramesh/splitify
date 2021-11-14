package com.splitify.splitify.api.expense.dto;

import com.splitify.splitify.security.service.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Calendar;

/** expense details dto */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetailsListDto {
  private Integer paymentId;
  private UserDetails receivedBy;
  private Integer groupId;
  private UserDetails paidBy;
  private UserDetails createdBy;
  private BigDecimal amount;
  private Calendar onDate;
}
