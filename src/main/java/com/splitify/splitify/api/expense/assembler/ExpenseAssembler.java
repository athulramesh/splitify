package com.splitify.splitify.api.expense.assembler;

import com.splitify.splitify.api.expense.dto.ExpenseDetailsDto;
import com.splitify.splitify.api.expense.dto.ExpenseRequestDto;
import com.splitify.splitify.api.expense.dto.GroupTransactionDetailsDto;
import com.splitify.splitify.api.expense.dto.IndividualTransactionDetailsDto;
import com.splitify.splitify.transaction.service.ExpenseDetails;
import com.splitify.splitify.transaction.service.ExpenseRequest;
import com.splitify.splitify.transaction.service.GroupTransactionDetails;
import com.splitify.splitify.transaction.service.IndividualTransactionDetails;
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
}
