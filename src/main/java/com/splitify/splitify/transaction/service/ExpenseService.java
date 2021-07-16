package com.splitify.splitify.transaction.service;

import com.querydsl.core.Tuple;
import com.splitify.splitify.api.expense.dto.TransactionDto;
import com.splitify.splitify.common.exception.ExceptionUtils;
import com.splitify.splitify.connection.service.GroupService;
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
import java.util.*;

/** The Expense service. */
@Service
public class ExpenseService {
  @Autowired private ExpenseRepository repository;
  @Autowired private ExceptionUtils exceptionUtils;
  @Autowired private UserService userService;
  @Autowired private GroupService groupService;

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
              .isExcessPayment(expenseRequest.getIsExcessPayment())
              .settledAmount(BigDecimal.ZERO)
              .build();
      expenseEntity.addExpenseShare(expenseRequest.getShare());
      Integer expenseId = repository.save(expenseEntity).getExpenseId();
      if (expenseRequest.getIsExcessPayment() != null && !expenseRequest.getIsExcessPayment()) {
        groupService.updateDebtsAfterExpenseAdd(expenseRequest.getGroupId());
      }
      return expenseId;
    }
    return null;
  }

  /**
   * Gets the expense request
   *
   * @param groupId groupId
   * @param paidBy paidBy
   * @param amount amount
   * @param ownerId ownerId
   * @return expense request
   */
  public ExpenseRequest getExpenseRequest(
      Integer groupId, Integer paidBy, BigDecimal amount, Integer ownerId) {
    return ExpenseRequest.builder()
        .amount(amount)
        .createdBy(paidBy)
        .expenseName("PAYMENT")
        .groupId(groupId)
        .onDate(Calendar.getInstance())
        .isExcessPayment(true)
        .paidBy(paidBy)
        .share(
            Collections.singletonList(
                ShareDetails.builder().amount(amount).ownerId(ownerId).build()))
        .build();
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
    List<ExpenseEntity> expenseEntities = repository.findByGroupIdAndPaidBy(groupId, receivedBy);
    List<PaymentShareVo> paymentShareVos = new ArrayList<>();
    if (groupService.isSimplifiedGroup(groupId)) {
      BigDecimal paidAmount = amount;
      for (ExpenseEntity expense : expenseEntities) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
          BigDecimal offsetAmount = expense.getOffsetAmount();
          if (amount.compareTo(offsetAmount) >= 0) {
            amount = amount.subtract(offsetAmount);
            expense.setSettledAmount(expense.getAmount());
            expense.setPaymentStatus(ExpensePaymentStatus.SETTLED.getCode());
          } else {
            expense.setSettledAmount(expense.getSettledAmount().add(amount));
            expense.setPaymentStatus(ExpensePaymentStatus.PARTIALLY_SETTLED.getCode());
            amount = BigDecimal.ZERO;
          }
          if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            break;
          }
        }
      }
      List<ExpenseEntity> payerExpenses = repository.getExpensesByOwner(groupId, paidBy);
      for (ExpenseEntity expense : payerExpenses) {
        if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
          paidAmount = expense.updateExpenseShareAmount(paidBy, paidAmount, paymentShareVos);
          if (paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            break;
          }
        }
      }
    } else {
      for (ExpenseEntity expense : expenseEntities) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
          amount = expense.updateExpenseShareAmount(paidBy, amount, paymentShareVos);
          if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            break;
          }
        }
      }
    }
    if (amount.compareTo(BigDecimal.ZERO) > 0) {
      ExpenseRequest request = getExpenseRequest(groupId, paidBy, amount, receivedBy);
      recordExpense(request);
    }
    repository.saveAll(expenseEntities);
    groupService.updateDebtsAfterExpenseAdd(groupId);
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
    Map<Integer, TransactionDto> nonGroup = new HashMap<>();
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
              if (isNonGroup(key)) {
                nonGroup.put(Integer.valueOf(key.split("-")[1]), transactionDto);
              } else {
                out.put(groupId, transactionDto);
              }
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
              if (isNonGroup(key)) {
                nonGroup.put(Integer.valueOf(key.split("-")[1]), transactionDto);
              } else {
                out.put(groupId, transactionDto);
              }
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
            if (isNonGroup(key)) {
              nonGroup.put(Integer.valueOf(key.split("-")[1]), transactionDto);
            } else {
              out.put(groupId, transactionDto);
            }
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
            if (isNonGroup(key)) {
              nonGroup.put(Integer.valueOf(key.split("-")[1]), transactionDto);
            } else {
              out.put(groupId, transactionDto);
            }
          });
    }
    return GroupTransactionDetails.builder()
        .groupTransaction(getGroupTransaction(out))
        .nonGroupTransaction(getGroupTransaction(nonGroup))
        .build();
  }

  private boolean isNonGroup(String key) {
    return key.split("-")[0].compareTo("null") == 0;
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

  /**
   * Gets effective amount
   *
   * @param groupId group id
   * @return effective amount
   */
  public Map<Integer, BigDecimal> getEffectiveAmount(Integer groupId) {
    List<ExpenseEntity> expenseEntities =
        repository.findByGroupIdAndPaymentStatusIn(
            groupId,
            Arrays.asList(
                ExpensePaymentStatus.PARTIALLY_SETTLED.getCode(),
                ExpensePaymentStatus.UNSETTLED.getCode()));
    Map<Integer, BigDecimal> effectiveAmount = new HashMap<>();
    expenseEntities.forEach(
        expenseEntity -> {
          Integer fromId = expenseEntity.getPaidBy();
          BigDecimal[] amount = {BigDecimal.ZERO};
          expenseEntity
              .getExpenseShare()
              .forEach(
                  share -> {
                    BigDecimal shareAmount = share.getRemainingAmount().negate();
                    amount[0] = amount[0].add(shareAmount.negate());
                    BigDecimal inAmount = effectiveAmount.get(share.getOwedBy());
                    shareAmount = inAmount != null ? shareAmount.add(inAmount) : shareAmount;
                    effectiveAmount.put(share.getOwedBy(), shareAmount);
                  });
          BigDecimal inAmount = effectiveAmount.get(fromId);
          amount[0] = inAmount != null ? amount[0].add(inAmount) : amount[0];
          effectiveAmount.put(fromId, amount[0]);
        });
    return effectiveAmount;
  }

  /**
   * Gets debts
   *
   * @param groupId groupId
   * @return debts
   */
  public List<DebtVo> getDebts(Integer groupId) {
    Map<Integer, BigDecimal> effectiveAmount = getEffectiveAmount(groupId);
    List<DebtVo> newDebts = new ArrayList<>();
    while (effectiveAmount.size() > 1) {
      AmountVo credit = getCredit(effectiveAmount);
      AmountVo debit = getDebit(effectiveAmount);
      BigDecimal reducingAmount = credit.getAmount().min(debit.getAmount().abs());
      if (reducingAmount.compareTo(credit.getAmount()) == 0) {
        newDebts.add(
            DebtVo.builder()
                .amount(reducingAmount)
                .fromId(debit.getFromId())
                .toId(credit.getFromId())
                .build());
        effectiveAmount.put(
            debit.getFromId(), effectiveAmount.get(debit.getFromId()).subtract(reducingAmount));
        effectiveAmount.remove(credit.getFromId());
      } else {
        newDebts.add(
            DebtVo.builder()
                .amount(reducingAmount)
                .fromId(debit.getFromId())
                .toId(credit.getFromId())
                .build());
        effectiveAmount.put(
            credit.getFromId(), effectiveAmount.get(credit.getFromId()).subtract(reducingAmount));
        effectiveAmount.remove(debit.getFromId());
      }
    }
    return newDebts;
  }

  /**
   * Get debit
   *
   * @param effectiveAmount effectiveAmount
   * @return debit
   */
  private AmountVo getDebit(Map<Integer, BigDecimal> effectiveAmount) {
    Integer[] id = {0};
    BigDecimal[] amount = {BigDecimal.ZERO};
    effectiveAmount.forEach(
        (key, value) -> {
          if (amount[0].compareTo(value) >= 0) {
            amount[0] = value;
            id[0] = key;
          }
        });
    return AmountVo.builder().amount(amount[0]).fromId(id[0]).build();
  }

  /**
   * Get Credits
   *
   * @param effectiveAmount effectiveAmount
   * @return credit
   */
  private AmountVo getCredit(Map<Integer, BigDecimal> effectiveAmount) {
    Integer[] id = {0};
    BigDecimal[] amount = {BigDecimal.ZERO};
    effectiveAmount.forEach(
        (key, value) -> {
          if (amount[0].compareTo(value) <= 0) {
            amount[0] = value;
            id[0] = key;
          }
        });
    return AmountVo.builder().amount(amount[0]).fromId(id[0]).build();
  }
}
