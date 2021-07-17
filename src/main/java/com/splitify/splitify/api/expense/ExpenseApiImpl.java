package com.splitify.splitify.api.expense;

import com.splitify.splitify.api.expense.assembler.ExpenseAssembler;
import com.splitify.splitify.api.expense.dto.ExpenseDetailsDto;
import com.splitify.splitify.api.expense.dto.ExpenseRequestDto;
import com.splitify.splitify.api.expense.dto.ExpenseResponseDto;
import com.splitify.splitify.api.expense.dto.UserExpenseDetailsDto;
import com.splitify.splitify.transaction.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api/expenses/")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class ExpenseApiImpl implements ExpenseApi {

  @Autowired private ExpenseService service;
  @Autowired private ExpenseAssembler assembler;
  /**
   * Record expense
   *
   * @param expenseRequest expenseRequest
   * @return Expense response
   */
  @Override
  public ExpenseResponseDto recordExpense(ExpenseRequestDto expenseRequest) {
    Integer expenseId = service.recordExpense(assembler.assembleExpenseRequestDto(expenseRequest));
    return ExpenseResponseDto.builder().expenseId(expenseId).build();
  }

  /**
   * Updates the expense
   *
   * @param expenseId expenseId
   * @param expenseRequest expenseRequest
   * @return expense response
   */
  @Override
  public ExpenseResponseDto updateExpense(Integer expenseId, ExpenseRequestDto expenseRequest) {
    Integer expense =
        service.updateExpense(expenseId, assembler.assembleExpenseRequestDto(expenseRequest));
    return ExpenseResponseDto.builder().expenseId(expense).build();
  }

  /**
   * Delete expense
   *
   * @param expenseId expenseId
   */
  @Override
  public void deleteExpense(Integer expenseId) {
    service.deleteExpense(expenseId);
  }

  /**
   * Get expense details.
   *
   * @param expenseId expenseId.
   * @return expense details.
   */
  @Override
  public ExpenseDetailsDto getExpenseDetails(Integer expenseId) {
    return assembler.assembleExpenseRequest(service.getExpenseDetails(expenseId));
  }

  /**
   * Get expenses.
   *
   * @param userId userId.
   * @return expense details.
   */
  @Override
  public UserExpenseDetailsDto getUserExpenses(Integer userId, Integer groupId) {
    return assembler.assembleUserExpenseRequest(service.getUserExpenses(userId, groupId));
  }
}
