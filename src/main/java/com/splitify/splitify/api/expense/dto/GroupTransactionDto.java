package com.splitify.splitify.api.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** expense details dto */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupTransactionDto {
  private Integer groupId;
  private TransactionDto transaction;
}
