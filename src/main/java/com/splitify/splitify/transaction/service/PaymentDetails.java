package com.splitify.splitify.transaction.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** expense details dto */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetails {
  private List<PaymentDetailsList> payments;
}
