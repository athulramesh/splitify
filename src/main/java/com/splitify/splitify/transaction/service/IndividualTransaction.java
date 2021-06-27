package com.splitify.splitify.transaction.service;

import com.splitify.splitify.security.service.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IndividualTransaction {
  private UserDetails person;
  private Boolean isPaidBy;
  private BigDecimal amount;
}
