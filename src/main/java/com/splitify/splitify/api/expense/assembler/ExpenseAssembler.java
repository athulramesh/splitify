package com.splitify.splitify.api.expense.assembler;

import com.splitify.splitify.api.expense.dto.*;
import com.splitify.splitify.transaction.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpenseAssembler {
  @Autowired private ModelMapper modelMapper;

  public ExpenseRequest assembleExpenseRequestDto(ExpenseRequestDto expenseRequest) {
    return modelMapper.map(expenseRequest, ExpenseRequest.class);
  }

  public ExpenseDetailsDto assembleExpenseRequest(ExpenseDetails expenseDetails) {
    return modelMapper.map(expenseDetails, ExpenseDetailsDto.class);
  }

  public GroupTransactionDetailsDto assembleGroupTransactionDetailsDto(
      GroupTransactionDetails groupWiseTransactions) {
    return modelMapper.map(groupWiseTransactions, GroupTransactionDetailsDto.class);
  }

  public IndividualTransactionDetailsDto assembleIndividualTransactionDetailsDto(
      IndividualTransactionDetails individualTransaction) {
    return modelMapper.map(individualTransaction, IndividualTransactionDetailsDto.class);
  }

  public UserExpenseDetailsDto assembleUserExpenseRequest(UserExpenseDetails userExpenses) {
    return modelMapper.map(userExpenses, UserExpenseDetailsDto.class);
  }
}
