package com.splitify.splitify.api.expense;

import com.splitify.splitify.api.expense.dto.ExpenseDetailsDto;
import com.splitify.splitify.api.expense.dto.ExpenseRequestDto;
import com.splitify.splitify.api.expense.dto.ExpenseResponseDto;
import com.splitify.splitify.api.expense.dto.UserExpenseDetailsDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/api/expenses/")
@CrossOrigin(origins = "https://simplifysplit.web.app", allowedHeaders = "*")
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

  /**
   * Get expenses.
   *
   * @param userId userId.
   * @return expense details.
   */
  @GetMapping("{userId}/group/{groupId}")
  UserExpenseDetailsDto getUserExpenses(
      @PathVariable("userId") Integer userId, @PathVariable("groupId") Integer groupId);
}
