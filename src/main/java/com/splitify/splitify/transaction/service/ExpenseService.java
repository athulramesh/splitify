package com.splitify.splitify.transaction.service;

import com.splitify.splitify.common.exception.ExceptionUtils;
import com.splitify.splitify.security.service.UserService;
import com.splitify.splitify.transaction.domain.ExpenseEntity;
import com.splitify.splitify.transaction.domain.ExpenseShareEntity;
import com.splitify.splitify.transaction.enums.ExpensePaymentStatus;
import com.splitify.splitify.transaction.enums.ExpenseStatus;
import com.splitify.splitify.transaction.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** The Expense service. */
@Service
public class ExpenseService {
  @Autowired private ExpenseRepository repository;
  @Autowired private ExceptionUtils exceptionUtils;
  @Autowired private UserService userService;

  /**
   * Records the expense
   *
   * @param expenseRequest expenseRequest
   * @return expense id
   */
  public Integer recordExpense(ExpenseRequest expenseRequest) {
    if (expenseRequest != null) {
      ExpenseEntity expenseEntity =
          ExpenseEntity.builder()
              .amount(expenseRequest.getAmount())
              .createdBy(expenseRequest.getCreatedBy())
              .groupId(expenseRequest.getGroupId())
              .expenseName(expenseRequest.getExpenseName())
              .paidBy(expenseRequest.getPaidBy())
              .status(ExpenseStatus.ACTIVE.getCode())
              .paymentStatus(ExpensePaymentStatus.UNSETTLED.getCode())
              .onDate(expenseRequest.getOnDate())
              .build();
      expenseEntity.addExpenseShare(expenseRequest.getShare());
      return repository.save(expenseEntity).getExpenseId();
    }
    return null;
  }

  /**
   * Updates the expense
   *
   * @param expenseId expenseId
   * @param expenseRequest expenseRequest
   * @return expense id
   */
  public Integer updateExpense(Integer expenseId, ExpenseRequest expenseRequest) {
    ExpenseEntity expenseEntity = getExpenseEntity(expenseId);
    if (expenseEntity != null) {
      expenseEntity.updateExpense(expenseRequest);
      repository.save(expenseEntity);
      return expenseEntity.getExpenseId();
    }
    return null;
  }

  /**
   * Get Expense entity
   *
   * @param expenseId expenseId
   * @return expense entity
   */
  private ExpenseEntity getExpenseEntity(Integer expenseId) {
    return repository.findById(expenseId).orElse(null);
  }

  /**
   * Delete expense
   *
   * @param expenseId expenseId
   */
  public void deleteExpense(Integer expenseId) {
    ExpenseEntity expenseEntity = getExpenseEntity(expenseId);
    if (expenseEntity != null) {
      expenseEntity.delete();
      repository.save(expenseEntity);
    }
  }

  /**
   * Get Expense details
   *
   * @param expenseId expenseId
   * @return expense details
   */
  public ExpenseDetails getExpenseDetails(Integer expenseId) {
    ExpenseEntity expenseEntity = getExpenseEntity(expenseId);
    if (expenseEntity != null) {
      return buildExpense(expenseEntity);
    }
    return null;
  }

  /**
   * builds the expense details
   *
   * @param expenseEntity expenseId
   * @return expense details
   */
  private ExpenseDetails buildExpense(ExpenseEntity expenseEntity) {
    return ExpenseDetails.builder()
        .amount(expenseEntity.getAmount())
        .createdBy(userService.getUserById(expenseEntity.getCreatedBy()))
        .groupId(expenseEntity.getGroupId())
        .paidBy(userService.getUserById(expenseEntity.getPaidBy()))
        .expenseName(expenseEntity.getExpenseName())
        .share(buildShare(expenseEntity.getExpenseShare()))
        .build();
  }

  /**
   * Builds the expense share
   *
   * @param expenseShare expenseShare
   * @return share list.
   */
  private List<ExpenseShareDetails> buildShare(List<ExpenseShareEntity> expenseShare) {
    List<ExpenseShareDetails> shareDetailsList = new ArrayList<>();
    expenseShare.forEach(
        expenseShareEntity ->
            shareDetailsList.add(
                ExpenseShareDetails.builder()
                    .amount(expenseShareEntity.getAmount())
                    .ownerId(userService.getUserById(expenseShareEntity.getOwedBy()))
                    .paidDate(expenseShareEntity.getPaidDate())
                    .settledAmount(expenseShareEntity.getSettledAmount())
                    .build()));
    return shareDetailsList;
  }
}
