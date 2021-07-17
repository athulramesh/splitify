package com.splitify.splitify.api.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** expense details dto */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserExpenseDetailsDto {
  private List<ExpenseDetailsDto> expenses;
}
