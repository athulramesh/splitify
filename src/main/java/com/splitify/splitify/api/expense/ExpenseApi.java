package com.splitify.splitify.api.expense;

import com.splitify.splitify.api.expense.dto.ExpenseDetailsDto;
import com.splitify.splitify.api.expense.dto.ExpenseRequestDto;
import com.splitify.splitify.api.expense.dto.ExpenseResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/api/expenses/")
public interface ExpenseApi {

  /**
   * Record expense
   *
   * @param expenseRequest expenseRequest
   * @return Expense response
   */
  @PostMapping
  ExpenseResponseDto recordExpense(@RequestBody ExpenseRequestDto expenseRequest);

  /**
   * Updates the expense
   *
   * @param expenseId expenseId
   * @param expenseRequest expenseRequest
   * @return expense response
   */
  @PutMapping("{expenseId}/")
  ExpenseResponseDto updateExpense(
      @PathVariable("expenseId") Integer expenseId, @RequestBody ExpenseRequestDto expenseRequest);

  /**
   * Delete expense
   *
   * @param expenseId expenseId
   */
  @PutMapping("{expenseId}/delete")
  void deleteExpense(@PathVariable("expenseId") Integer expenseId);

  /**
   * Get expense details.
   *
   * @param expenseId expenseId.
   * @return expense details.
   */
  @GetMapping("{expenseId}/")
  ExpenseDetailsDto getExpenseDetails(@PathVariable("expenseId") Integer expenseId);
}
