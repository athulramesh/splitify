package com.splitify.splitify.transaction.service;

import com.querydsl.core.Tuple;
import com.splitify.splitify.api.expense.dto.TransactionDto;
import com.splitify.splitify.common.exception.ExceptionUtils;
import com.splitify.splitify.connection.domain.GroupEntity;
import com.splitify.splitify.connection.service.GroupService;
import com.splitify.splitify.security.service.UserDetails;
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
import java.util.stream.Collectors;

/** The Expense service. */
@Service
public class ExpenseService {
  @Autowired private ExpenseRepository repository;
  @Autowired private ExceptionUtils exceptionUtils;
  @Autowired private UserService userService;
  @Autowired private GroupService groupService;

  private static int sorted(ExpenseDetails e1, ExpenseDetails e2) {
    return e1.getOnDate().compareTo(e2.getOnDate());
  }

  /**
   * Records the expense
   *
   * @param expenseRequest expenseRequest
   * @return expense id
   */
  public Integer recordExpense(ExpenseRequest expenseRequest) {
    if (expenseRequest != null) {
      BigDecimal dueAmount =
          expenseRequest.getShare().stream()
              .map(ShareDetails::getAmount)
              .reduce(BigDecimal.ZERO, BigDecimal::add);
      ExpenseEntity expenseEntity =
          ExpenseEntity.builder()
              .amount(expenseRequest.getAmount())
              .dueAmount(dueAmount)
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
      if (expenseRequest.getIsExcessPayment() == null
          || (expenseRequest.getIsExcessPayment() != null
              && !expenseRequest.getIsExcessPayment())) {
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
        .expenseId(expenseEntity.getExpenseId())
        .amount(expenseEntity.getAmount())
        //        .settledAmount(expenseEntity.getSettledAmount())
        .settledAmount(expenseEntity.getOffsetAmount())
        .createdBy(userService.getUserById(expenseEntity.getCreatedBy()))
        .groupId(expenseEntity.getGroupId())
        .paidBy(userService.getUserById(expenseEntity.getPaidBy()))
        .expenseName(expenseEntity.getExpenseName())
        .share(buildShare(expenseEntity.getExpenseShare()))
        .onDate(expenseEntity.getOnDate())
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
                    .remainingAmount(expenseShareEntity.getRemainingAmount())
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
    BigDecimal receivedAmount = amount;
    boolean isSimplified = groupService.isSimplifiedGroup(groupId);
    if (isSimplified) {
      BigDecimal paidAmount = amount;
      for (ExpenseEntity expense : expenseEntities) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
          BigDecimal offsetAmount = expense.getOffsetAmount();
          if (amount.compareTo(offsetAmount) >= 0) {
            amount = amount.subtract(offsetAmount);
            expense.setSettledAmount(expense.getDueAmount());
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
          paidAmount = expense.updateExpenseShareAmount(paidBy, paidAmount, paymentShareVos, true);
          if (paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            break;
          }
        }
      }
    } else {
      for (ExpenseEntity expense : expenseEntities) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
          amount = expense.updateExpenseShareAmount(paidBy, amount, paymentShareVos, false);
          if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            break;
          }
        }
      }
    }
    if (amount.compareTo(BigDecimal.ZERO) > 0) {
      ExpenseRequest request = getExpenseRequest(groupId, paidBy, amount, receivedBy);
      recordExpense(request);
    } else {
      groupService.updateDebtsAfterPayment(groupId, paidBy, receivedBy, receivedAmount);
      GroupEntity groupEntity = groupService.getGroupById(groupId);
      if (isSimplified && CollectionUtils.isEmpty(groupEntity.getDebt())) {
        List<ExpenseEntity> unsettled =
            repository.findByGroupIdAndPaymentStatusIn(
                groupId,
                Arrays.asList(
                    ExpensePaymentStatus.PARTIALLY_SETTLED.getCode(),
                    ExpensePaymentStatus.UNSETTLED.getCode()));
        unsettled.forEach(u -> u.setPaymentStatus(ExpensePaymentStatus.SETTLED.getCode()));
      }
    }
    repository.saveAll(expenseEntities);
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
    Map<Integer, String> groupMap = new HashMap<>();
    Map<Integer, Integer> individualGroup = new HashMap<>();
    fromTransactions.forEach(
        t -> {
          String key =
              t.get(3, String.class).compareTo("INDIVIDUAL") != 0
                  ? t.get(0, Integer.class) + "-" + t.get(1, Integer.class)
                  : "null" + "-" + t.get(1, Integer.class);
          map.put(key, t.get(2, BigDecimal.class));
          if (t.get(3, String.class).compareTo("INDIVIDUAL") != 0) {
            groupMap.put(t.get(0, Integer.class), t.get(3, String.class));
          } else {
            individualGroup.put(t.get(1, Integer.class), t.get(0, Integer.class));
          }
        });
    List<Tuple> toTransactions = repository.getTotalPayableAmountPerGroup(fromId);
    Map<Integer, TransactionDto> out = new HashMap<>();
    Map<Integer, TransactionDto> nonGroup = new HashMap<>();
    toTransactions.forEach(
        t -> {
          Integer groupId = t.get(0, Integer.class);
          BigDecimal toAmount = t.get(2, BigDecimal.class);
          toAmount = toAmount != null ? toAmount : BigDecimal.ZERO;
          String key =
              t.get(3, String.class).compareTo("INDIVIDUAL") != 0
                  ? t.get(0, Integer.class) + "-" + t.get(1, Integer.class)
                  : "null" + "-" + t.get(1, Integer.class);
          if (t.get(3, String.class).compareTo("INDIVIDUAL") != 0) {
            groupMap.put(t.get(0, Integer.class), t.get(3, String.class));
          } else {
            individualGroup.put(t.get(1, Integer.class), t.get(0, Integer.class));
          }
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
            boolean isNonGroup = isNonGroup(key);
            Integer groupId = isNonGroup ? -1 : Integer.parseInt(key.split("-")[0]);
            TransactionDto transactionDto;
            if (out.containsKey(groupId)) {
              transactionDto = out.get(groupId);
              transactionDto.setFromAmount(transactionDto.getFromAmount().add(value));
            } else {
              transactionDto =
                  TransactionDto.builder().fromAmount(value).toAmount(BigDecimal.ZERO).build();
            }
            if (isNonGroup) {
              nonGroup.put(Integer.valueOf(key.split("-")[1]), transactionDto);
            } else {
              out.put(groupId, transactionDto);
            }
          });
    }
    List<GroupTransaction> transactions = getGroupTransaction(out, groupMap, null);
    getSimplifiedGroupTransactions(transactions, fromId);
    List<GroupTransaction> nonGroups = getGroupTransaction(nonGroup, null, individualGroup);
    BigDecimal[] fromAmount = {BigDecimal.ZERO};
    BigDecimal[] toAmount = {BigDecimal.ZERO};
    transactions.forEach(
        t -> {
          fromAmount[0] =
              fromAmount[0].add(
                  t.getTransaction().getFromAmount() != null
                      ? t.getTransaction().getFromAmount()
                      : BigDecimal.ZERO);
          toAmount[0] =
              toAmount[0].add(
                  t.getTransaction().getToAmount() != null
                      ? t.getTransaction().getToAmount()
                      : BigDecimal.ZERO);
        });
    nonGroups.forEach(
        t -> {
          fromAmount[0] =
              fromAmount[0].add(
                  t.getTransaction().getFromAmount() != null
                      ? t.getTransaction().getFromAmount()
                      : BigDecimal.ZERO);
          toAmount[0] =
              toAmount[0].add(
                  t.getTransaction().getToAmount() != null
                      ? t.getTransaction().getToAmount()
                      : BigDecimal.ZERO);
        });
    return GroupTransactionDetails.builder()
        .groupTransaction(transactions)
        .nonGroupTransaction(nonGroups)
        .fromAmount(fromAmount[0])
        .toAmount(toAmount[0])
        .build();
  }

  /**
   * Get simplified group transaction
   *
   * @param transactions transactions
   * @param fromId fromId
   */
  private void getSimplifiedGroupTransactions(List<GroupTransaction> transactions, Integer fromId) {
    List<GroupTransaction> simplifiedTransactions =
        groupService.getSimplifiedTransactionsForUser(fromId);
    transactions.addAll(simplifiedTransactions);
  }

  /**
   * is Non group
   *
   * @param key key
   * @return is non group
   */
  private boolean isNonGroup(String key) {
    return key.split("-")[0].compareTo("null") == 0;
  }

  /**
   * Get group Transaction
   *
   * @param out out
   * @param groupMap groupMap
   * @param individualGroup individualGroup
   * @return list of group transaction
   */
  private List<GroupTransaction> getGroupTransaction(
      Map<Integer, TransactionDto> out,
      Map<Integer, String> groupMap,
      Map<Integer, Integer> individualGroup) {
    List<GroupTransaction> transactions = new ArrayList<>();
    if (groupMap != null) {
      out.forEach(
          (key, value) -> {
            if (!(value.getFromAmount().compareTo(BigDecimal.ZERO) == 0
                && value.getToAmount().compareTo(BigDecimal.ZERO) == 0)) {
              transactions.add(
                  GroupTransaction.builder()
                      .groupId(key)
                      .transaction(value)
                      .groupName(groupMap.get(key))
                      .user(null)
                      .build());
            }
          });
    } else {
      out.forEach(
          (key, value) -> {
            if (!(value.getFromAmount().compareTo(BigDecimal.ZERO) == 0
                && value.getToAmount().compareTo(BigDecimal.ZERO) == 0)) {
              UserDetails user = userService.getUserById(key);
              transactions.add(
                  GroupTransaction.builder()
                      .groupId(individualGroup.get(key))
                      .transaction(value)
                      .user(user)
                      .groupName(null)
                      .build());
            }
          });
    }
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
    List<IndividualTransaction> individualTransactions = new ArrayList<>();
    GroupEntity groupEntity = groupService.getGroupById(groupId);
    if (groupEntity.getIsSimplified()) {
      groupEntity
          .getDebtOfUser(fromId)
          .forEach(
              d -> {
                individualTransactions.add(
                    IndividualTransaction.builder()
                        .amount(d.getAmount())
                        .isToPay(d.getFromId().compareTo(fromId) == 0)
                        .person(
                            userService.getUserById(
                                d.getFromId().compareTo(fromId) == 0 ? d.getToId() : d.getFromId()))
                        .build());
              });
      return IndividualTransactionDetails.builder()
          .individualTransaction(individualTransactions)
          .build();
    }
    List<Tuple> fromTransactions = repository.getTotalDueAmountInGroup(fromId, groupId);
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
          if (amount.compareTo(BigDecimal.ZERO) > 0) {
            individualTransactions.add(
                IndividualTransaction.builder()
                    .amount(amount)
                    .isToPay(isToPay)
                    .person(userService.getUserById(t.get(0, Integer.class)))
                    .build());
          }
        });
    if (!CollectionUtils.isEmpty(map)) {
      map.forEach(
          (key, value) -> {
            if (value.compareTo(BigDecimal.ZERO) > 0) {
              individualTransactions.add(
                  IndividualTransaction.builder()
                      .amount(value)
                      .isToPay(Boolean.FALSE)
                      .person(userService.getUserById(key))
                      .build());
            }
          });
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

  /**
   * Get user expenses
   *
   * @param userId userId
   * @param groupId groupId
   * @return user expenses
   */
  public UserExpenseDetails getUserExpenses(Integer userId, Integer groupId) {
    GroupEntity groupEntity = groupService.getGroupById(groupId);
    if (groupEntity.getIsSimplified() && CollectionUtils.isEmpty(groupEntity.getDebt())) {
      return UserExpenseDetails.builder().expenses(Collections.emptyList()).build();
    }
    List<ExpenseEntity> expenseEntities =
        repository.findByGroupIdAndPaidByAndPaymentStatusIn(
            groupId,
            userId,
            Arrays.asList(
                ExpensePaymentStatus.PARTIALLY_SETTLED.getCode(),
                ExpensePaymentStatus.UNSETTLED.getCode()));
    List<ExpenseEntity> expenseSharedEntities = repository.getExpensesSharedOfUser(userId, groupId);
    List<ExpenseDetails> expenses = new ArrayList<>();
    expenseEntities.forEach(
        expenseEntity -> {
          expenses.add(buildExpense(expenseEntity));
        });
    expenseSharedEntities.forEach(
        expenseEntity -> {
          expenses.add(buildExpense(expenseEntity));
        });
    return UserExpenseDetails.builder()
        .expenses(expenses.stream().sorted(ExpenseService::sorted).collect(Collectors.toList()))
        .build();
  }
}
