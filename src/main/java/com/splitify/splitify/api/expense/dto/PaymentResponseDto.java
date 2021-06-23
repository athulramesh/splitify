package com.splitify.splitify.api.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** expense response dto */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponseDto {
  private Integer paymentId;
}
