package com.splitify.splitify.transaction.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmountVo {
  private Integer fromId;
  private BigDecimal amount;
}
