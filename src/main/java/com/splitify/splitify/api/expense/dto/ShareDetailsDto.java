package com.splitify.splitify.api.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
/** share details dto */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareDetailsDto {
  private Integer ownerId;
  private BigDecimal amount;
}
