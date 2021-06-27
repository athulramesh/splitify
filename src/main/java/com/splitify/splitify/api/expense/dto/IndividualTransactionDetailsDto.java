package com.splitify.splitify.api.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndividualTransactionDetailsDto {
  private List<IndividualTransactionDto> individualTransaction;
}
