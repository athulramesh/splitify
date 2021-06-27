package com.splitify.splitify.transaction.service;

import com.querydsl.core.Tuple;
import com.splitify.splitify.api.expense.dto.TransactionDto;
import com.splitify.splitify.common.exception.ExceptionUtils;
import com.splitify.splitify.security.service.UserService;
import com.splitify.splitify.transaction.domain.ExpenseEntity;
import com.splitify.splitify.transaction.domain.ExpenseShareEntity;
import com.splitify.splitify.transaction.enums.ExpensePaymentStatus;
import com.splitify.splitify.transaction.enums.ExpenseStatus;
import com.splitify.splitify.transaction.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  /**
   * Settle expenses
   *
   * @param groupId groupId
   * @param paidBy paidBy
   * @param receivedBy receivedBy
   * @param amount amount
   * @return PaymentShareVo list
   */
  public List<PaymentShareVo> settleExpense(
      Integer groupId, Integer paidBy, Integer receivedBy, BigDecimal amount) {
    List<ExpenseEntity> expenseEntities = repository.findByGroupIdAndPaidBy(groupId, paidBy);
    List<PaymentShareVo> paymentShareVos = new ArrayList<>();
    for (ExpenseEntity expense : expenseEntities) {
      if (amount.compareTo(BigDecimal.ZERO) > 0) {
        amount = expense.updateExpenseShareAmount(receivedBy, amount, paymentShareVos);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
          break;
        }
      }
    }
    return paymentShareVos;
  }

  /**
   * Update payment deduction
   *
   * @param shareVos shareVos
   */
  public void updatePaymentDeduction(List<PaymentShareVo> shareVos) {
    shareVos.forEach(
        share -> {
          ExpenseEntity expenseEntity = getExpenseEntity(share.getExpenseId());
          expenseEntity.updateExpenseShareDeduction(share);
        });
  }

  /**
   * Get Group wise Transaction.
   *
   * @param fromId fromId.
   * @return group transaction details.
   */
  public GroupTransactionDetails getGroupWiseTransactions(Integer fromId) {
    List<Tuple> fromTransactions = repository.getTotalDueAmountPerGroup(fromId);
    Map<String, BigDecimal> map = new HashMap<>();
    fromTransactions.forEach(
        t -> {
          String key = t.get(0, Integer.class) + "-" + t.get(1, Integer.class);
          map.put(key, t.get(2, BigDecimal.class));
        });
    List<Tuple> toTransactions = repository.getTotalPayableAmountPerGroup(fromId);
    Map<Integer, TransactionDto> out = new HashMap<>();
    toTransactions.forEach(
        t -> {
          Integer groupId = t.get(0, Integer.class);
          BigDecimal toAmount = t.get(2, BigDecimal.class);
          toAmount = toAmount != null ? toAmount : BigDecimal.ZERO;
          String key = t.get(0, Integer.class) + "-" + t.get(1, Integer.class);
          BigDecimal fromAmount = map.get(key);
          if (fromAmount != null) {
            if (fromAmount.compareTo(toAmount) > 0) {
              TransactionDto transactionDto;
              if (out.containsKey(groupId)) {
                transactionDto = out.get(groupId);
                transactionDto.setFromAmount(
                    transactionDto.getFromAmount().add(fromAmount.subtract(toAmount)));
              } else {
                transactionDto =
                    TransactionDto.builder()
                        .fromAmount(fromAmount.subtract(toAmount))
                        .toAmount(BigDecimal.ZERO)
                        .build();
              }
              out.put(groupId, transactionDto);
            } else {
              TransactionDto transactionDto;
              if (out.containsKey(groupId)) {
                transactionDto = out.get(groupId);
                transactionDto.setToAmount(
                    transactionDto.getToAmount().add(toAmount.subtract(fromAmount)));
              } else {
                transactionDto =
                    TransactionDto.builder()
                        .fromAmount(BigDecimal.ZERO)
                        .toAmount(toAmount.subtract(fromAmount))
                        .build();
              }
              out.put(groupId, transactionDto);
            }
            map.remove(key);
          } else {
            TransactionDto transactionDto;
            if (out.containsKey(groupId)) {
              transactionDto = out.get(groupId);
              transactionDto.setToAmount(transactionDto.getToAmount().add(toAmount));
            } else {
              transactionDto =
                  TransactionDto.builder().fromAmount(BigDecimal.ZERO).toAmount(toAmount).build();
            }
            out.put(groupId, transactionDto);
          }
        });
    if (!CollectionUtils.isEmpty(map)) {
      map.forEach(
          (key, value) -> {
            Integer groupId = Integer.parseInt(key.split("-")[0]);
            TransactionDto transactionDto;
            if (out.containsKey(groupId)) {
              transactionDto = out.get(groupId);
              transactionDto.setFromAmount(transactionDto.getFromAmount().add(value));
            } else {
              transactionDto =
                  TransactionDto.builder().fromAmount(value).toAmount(BigDecimal.ZERO).build();
            }
            out.put(groupId, transactionDto);
          });
    }
    return GroupTransactionDetails.builder().groupTransaction(getGroupTransaction(out)).build();
  }

  /**
   * Get group Transaction
   *
   * @param out out
   * @return list of group transaction
   */
  private List<GroupTransaction> getGroupTransaction(Map<Integer, TransactionDto> out) {
    List<GroupTransaction> transactions = new ArrayList<>();
    out.forEach(
        (key, value) -> {
          transactions.add(GroupTransaction.builder().groupId(key).transaction(value).build());
        });
    return transactions;
  }

  /**
   * Get Individual transaction
   *
   * @param fromId fromId
   * @param groupId groupId
   * @return individual transaction.
   */
  public IndividualTransactionDetails getIndividualTransaction(Integer fromId, Integer groupId) {
    List<Tuple> fromTransactions = repository.getTotalDueAmountInGroup(fromId, groupId);
    List<IndividualTransaction> individualTransactions = new ArrayList<>();
    Map<Integer, BigDecimal> map = new HashMap<>();
    fromTransactions.forEach(
        t -> {
          map.put(t.get(0, Integer.class), t.get(1, BigDecimal.class));
        });
    List<Tuple> toTransactions = repository.getTotalPayableAmountInGroup(fromId, groupId);
    toTransactions.forEach(
        t -> {
          BigDecimal amount = BigDecimal.ZERO;
          Boolean isToPay = Boolean.TRUE;
          Integer id = t.get(0, Integer.class);
          BigDecimal toAmount = t.get(1, BigDecimal.class);
          if (map.containsKey(id)) {
            BigDecimal fromAmount = map.get(id);
            if (fromAmount.compareTo(toAmount) >= 0) {
              amount = fromAmount.subtract(toAmount);
              isToPay = Boolean.FALSE;
            } else {
              amount = toAmount != null ? toAmount.subtract(fromAmount) : BigDecimal.ZERO;
            }
            map.remove(id);
          }
          individualTransactions.add(
              IndividualTransaction.builder()
                  .amount(amount)
                  .isPaidBy(isToPay)
                  .person(userService.getUserById(t.get(0, Integer.class)))
                  .build());
        });
    if (!CollectionUtils.isEmpty(map)) {
      map.forEach(
          (key, value) ->
              individualTransactions.add(
                  IndividualTransaction.builder()
                      .amount(value)
                      .isPaidBy(Boolean.FALSE)
                      .person(userService.getUserById(key))
                      .build()));
    }
    return IndividualTransactionDetails.builder()
        .individualTransaction(individualTransactions)
        .build();
  }
}
