package com.splitify.splitify.transaction.domain;

import com.splitify.splitify.transaction.enums.ExpensePaymentStatus;
import com.splitify.splitify.transaction.enums.ExpenseSharePaymentStatus;
import com.splitify.splitify.transaction.enums.ExpenseShareStatus;
import com.splitify.splitify.transaction.enums.ExpenseStatus;
import com.splitify.splitify.transaction.service.ExpenseRequest;
import com.splitify.splitify.transaction.service.PaymentShareVo;
import com.splitify.splitify.transaction.service.ShareDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EXPENSE")
@Builder
public class ExpenseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "EXPENSEID")
  private Integer expenseId;

  @Column(name = "EXPENSENAME")
  private String expenseName;

  @Column(name = "GROUPID")
  private Integer groupId;

  @Column(name = "AMOUNT")
  private BigDecimal amount;

  @Column(name = "DUEAMOUNT")
  private BigDecimal dueAmount;

  @Column(name = "SETTLEDAMOUNT")
  private BigDecimal settledAmount;

  @Column(name = "STATUS")
  private Integer status;

  @Column(name = "PAYMENTSTATUS")
  private Integer paymentStatus;

  @Column(name = "ONDATE")
  private Calendar onDate;

  @Column(name = "PAIDBY")
  private Integer paidBy;

  @Column(name = "CREATEDBY")
  private Integer createdBy;

  @Column(name = "ISEXCESSPAYMENT")
  private Boolean isExcessPayment;

  @OneToMany(
      mappedBy = "expense",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<ExpenseShareEntity> expenseShare;

  /**
   * Add expense share.
   *
   * @param shareDetails shareDetails.
   */
  public void addExpenseShare(List<ShareDetails> shareDetails) {
    if (expenseShare == null) {
      expenseShare = new ArrayList<>();
    }
    if (shareDetails != null) {
      shareDetails.forEach(
          share -> {
            expenseShare.add(
                ExpenseShareEntity.builder()
                    .amount(share.getAmount())
                    .owedBy(share.getOwnerId())
                    .settledAmount(BigDecimal.ZERO)
                    .status(ExpenseShareStatus.ACTIVE.getCode())
                    .paymentStatus(ExpenseSharePaymentStatus.UNSETTLED.getCode())
                    .expense(this)
                    .build());
          });
    }
  }

  /**
   * Updates the expense
   *
   * @param expenseRequest expenseRequests
   */
  public void updateExpense(ExpenseRequest expenseRequest) {
    setAmount(expenseRequest.getAmount());
    setDueAmount(expenseRequest.getDueAmount());
    setGroupId(expenseRequest.getGroupId());
    setExpenseName(expenseRequest.getExpenseName());
    setPaidBy(expenseRequest.getPaidBy());
    setOnDate(expenseRequest.getOnDate());
    updateExpenseShare(expenseRequest.getShare());
  }

  /**
   * Updates the expense share
   *
   * @param share share
   */
  private void updateExpenseShare(List<ShareDetails> share) {
    Map<Integer, BigDecimal> updateIds =
        share.stream().collect(Collectors.toMap(ShareDetails::getOwnerId, ShareDetails::getAmount));
    Set<Integer> oldIds =
        expenseShare.stream().map(ExpenseShareEntity::getOwedBy).collect(Collectors.toSet());
    List<ExpenseShareEntity> oldShares = new ArrayList<>();
    List<ShareDetails> newShares = new ArrayList<>();
    expenseShare.forEach(
        expenseShareEntity -> {
          if (!updateIds.containsKey(expenseShareEntity.getOwedBy())) {
            oldShares.add(expenseShareEntity);
          } else {
            expenseShareEntity.setAmount(updateIds.get(expenseShareEntity.getOwedBy()));
          }
        });
    share.forEach(
        shareDetails -> {
          if (!oldIds.contains(shareDetails.getOwnerId())) {
            newShares.add(shareDetails);
          }
        });
    if (!CollectionUtils.isEmpty(newShares)) {
      addExpenseShare(newShares);
    }
    if (!CollectionUtils.isEmpty(oldShares)) {
      expenseShare.removeAll(oldShares);
    }
  }

  /** Delete expense. */
  public void delete() {
    setStatus(ExpenseStatus.CANCELLED.getCode());
    expenseShare.forEach(expense -> expense.setStatus(ExpenseShareStatus.CANCELLED.getCode()));
  }

  /**
   * updates the expense share amount
   *
   * @param receivedBy receivedBy
   * @param amount amount
   * @param paymentShareVos paymentShareVos
   * @return amount
   */
  public BigDecimal updateExpenseShareAmount(
      Integer receivedBy, BigDecimal amount, List<PaymentShareVo> paymentShareVos) {
    for (ExpenseShareEntity share : expenseShare) {
      if (share.getOwedBy().equals(receivedBy)
          && share.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0) {
        BigDecimal remainingAmount = share.getRemainingAmount();
        if (amount.compareTo(remainingAmount) >= 0) {
          share.setSettledAmount(share.getAmount());
          setSettledAmount(getSettledAmount().add(remainingAmount));
          share.setPaymentStatus(ExpenseSharePaymentStatus.SETTLED.getCode());
        } else {
          share.setSettledAmount(share.getSettledAmount().add(amount));
          setSettledAmount(getSettledAmount().add(amount));
          share.setPaymentStatus(ExpenseSharePaymentStatus.PARTIALLY_SETTLED.getCode());
        }
        paymentShareVos.add(
            PaymentShareVo.builder()
                .expenseId(getExpenseId())
                .expenseShareId(share.getExpenseShareId())
                .amount(remainingAmount)
                .build());
        amount = amount.subtract(remainingAmount);
      }
      if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        break;
      }
    }
    updateStatus();
    return amount;
  }

  /** Update payment status */
  private void updateStatus() {
    BigDecimal offsetAmount = getOffsetAmount();
    if (offsetAmount.compareTo(BigDecimal.ZERO) == 0) {
      setPaymentStatus(ExpensePaymentStatus.SETTLED.getCode());
    } else if (offsetAmount.compareTo(dueAmount) == 0) {
      setPaymentStatus(ExpensePaymentStatus.UNSETTLED.getCode());
    } else {
      setPaymentStatus(ExpensePaymentStatus.PARTIALLY_SETTLED.getCode());
    }
  }

  /**
   * Update expense share deduction
   *
   * @param share share
   */
  public void updateExpenseShareDeduction(PaymentShareVo share) {
    expenseShare.forEach(
        expense -> {
          if (expense.getExpenseShareId().equals(share.getExpenseShareId())) {
            expense.setSettledAmount(expense.getSettledAmount().subtract(share.getAmount()));
            if (expense.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0) {
              expense.setPaymentStatus(ExpenseSharePaymentStatus.PARTIALLY_SETTLED.getCode());
            } else {
              expense.setPaymentStatus(ExpenseSharePaymentStatus.UNSETTLED.getCode());
            }
          }
        });
  }

  /**
   * update expense share amount for simplified group
   *
   * @param amount amount
   * @param paymentShareVos paymentShareVos
   * @return Amount
   */
  public BigDecimal updateExpenseShareAmountForSimplified(
      BigDecimal amount, List<PaymentShareVo> paymentShareVos) {
    for (ExpenseShareEntity share : expenseShare) {
      if (share.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0) {
        BigDecimal remainingAmount = share.getRemainingAmount();
        if (amount.compareTo(remainingAmount) >= 0) {
          share.setSettledAmount(share.getAmount());
          share.setPaymentStatus(ExpenseSharePaymentStatus.SETTLED.getCode());
        } else {
          share.setSettledAmount(share.getSettledAmount().add(amount));
          share.setPaymentStatus(ExpenseSharePaymentStatus.PARTIALLY_SETTLED.getCode());
        }
        paymentShareVos.add(
            PaymentShareVo.builder()
                .expenseId(getExpenseId())
                .expenseShareId(share.getExpenseShareId())
                .amount(remainingAmount)
                .build());
        amount = amount.subtract(remainingAmount);
      }
      if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        break;
      }
    }
    return amount;
  }

  public BigDecimal getOffsetAmount() {
    return getDueAmount().subtract(getSettledAmount());
  }
}
