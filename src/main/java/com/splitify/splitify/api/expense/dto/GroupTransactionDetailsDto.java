package com.splitify.splitify.api.expense.dto;

import com.splitify.splitify.transaction.service.GroupTransaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/** expense details dto */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupTransactionDetailsDto {
  private List<GroupTransactionDto> groupTransaction;
  private List<GroupTransaction> nonGroupTransaction;
  private BigDecimal fromAmount;
  private BigDecimal toAmount;
}
