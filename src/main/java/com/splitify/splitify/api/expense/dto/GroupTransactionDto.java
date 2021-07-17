package com.splitify.splitify.api.expense.dto;

import com.splitify.splitify.security.service.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** expense details dto */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupTransactionDto {
  private Integer groupId;
  private String groupName;
  private UserDetails user;
  private TransactionDto transaction;
}
