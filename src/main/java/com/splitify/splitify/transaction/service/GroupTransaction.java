package com.splitify.splitify.transaction.service;

import com.splitify.splitify.api.expense.dto.TransactionDto;
import com.splitify.splitify.security.service.UserDetails;
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
  private String groupName;
  private UserDetails user;
  private TransactionDto transaction;
}
