package com.splitify.splitify.transaction.service;

import com.splitify.splitify.api.expense.dto.TransactionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** expense details dto */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupTransaction {
  private Integer groupId;
  private TransactionDto transaction;
}
