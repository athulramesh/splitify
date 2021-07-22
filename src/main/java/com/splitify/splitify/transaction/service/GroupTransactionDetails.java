package com.splitify.splitify.transaction.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/** expense details dto */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupTransactionDetails {
  private List<GroupTransaction> groupTransaction;
  private List<GroupTransaction> nonGroupTransaction;
  private BigDecimal fromAmount;
  private BigDecimal toAmount;
}
