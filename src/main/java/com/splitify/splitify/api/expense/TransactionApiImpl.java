package com.splitify.splitify.api.expense;

import com.splitify.splitify.api.expense.assembler.ExpenseAssembler;
import com.splitify.splitify.api.expense.dto.GroupTransactionDetailsDto;
import com.splitify.splitify.api.expense.dto.IndividualTransactionDetailsDto;
import com.splitify.splitify.transaction.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/transactions")
public class TransactionApiImpl implements TransactionApi {
  @Autowired ExpenseService expenseService;
  @Autowired ExpenseAssembler assembler;

  /**
   * Gets the group wise transactions
   *
   * @param fromId fromId
   * @return group wise transaction
   */
  @Override
  public GroupTransactionDetailsDto getGroupWiseTransactions(Integer fromId) {
    return assembler.assembleGroupTransactionDetailsDto(
        expenseService.getGroupWiseTransactions(fromId));
  }

  /**
   * Gets the individual transaction.
   *
   * @param fromId fromId
   * @param groupId groupId
   * @return individual transaction.
   */
  @Override
  public IndividualTransactionDetailsDto getIndividualTransaction(Integer fromId, Integer groupId) {
    return assembler.assembleIndividualTransactionDetailsDto(
        expenseService.getIndividualTransaction(fromId, groupId));
  }
}
