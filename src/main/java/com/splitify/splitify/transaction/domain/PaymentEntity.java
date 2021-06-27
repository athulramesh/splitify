package com.splitify.splitify.transaction.domain;

import com.splitify.splitify.transaction.enums.PaymentStatus;
import com.splitify.splitify.transaction.service.PaymentRequest;
import com.splitify.splitify.transaction.service.PaymentShareVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PAYMENT")
@Builder
public class PaymentEntity {

  @OneToMany(
      mappedBy = "payment",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  List<PaymentShareEntity> paymentShareEntities;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "PAYMENTID")
  private Integer paymentId;

  @Column(name = "FROMID")
  private Integer fromId;

  @Column(name = "TOID")
  private Integer toId;

  @Column(name = "AMOUNT")
  private BigDecimal amount;

  @Column(name = "ONDATE")
  private Calendar onDate;

  @Column(name = "CREATEDBY")
  private Integer createdBy;

  @Column(name = "GROUPID")
  private Integer groupId;

  @Column(name = "STATUS")
  private Integer status;

  /**
   * Update payment
   *
   * @param paymentRequest paymentRequest
   */
  public void updatePayment(PaymentRequest paymentRequest) {
    setAmount(paymentRequest.getAmount());
    setFromId(paymentRequest.getPaidBy());
    setToId(paymentRequest.getReceivedBy());
    setGroupId(paymentRequest.getGroupId());
    setOnDate(paymentRequest.getOnDate());
  }

  /** Cancel the payment */
  public void delete() {
    setStatus(PaymentStatus.CANCELLED.getCode());
  }

  /**
   * Add payment share
   *
   * @param shareVos shareVos
   */
  public void addPaymentShare(List<PaymentShareVo> shareVos) {
    if (paymentShareEntities == null) {
      paymentShareEntities = new ArrayList<>();
    }
    shareVos.forEach(
        share -> {
          paymentShareEntities.add(
              PaymentShareEntity.builder()
                  .expenseId(share.getExpenseId())
                  .expenseShareId(share.getExpenseShareId())
                  .amount(share.getAmount())
                  .payment(this)
                  .build());
        });
  }

  /**
   * Updates the payment share
   *
   * @param deductedAmount deductedAmount
   * @return PaymentShareVo
   */
  public List<PaymentShareVo> updatePaymentShare(BigDecimal deductedAmount) {
    List<PaymentShareVo> shareVos = new ArrayList<>();
    for (PaymentShareEntity paymentShare : paymentShareEntities) {
      if (deductedAmount.compareTo(paymentShare.getAmount()) >= 0) {

        shareVos.add(
            PaymentShareVo.builder()
                .amount(paymentShare.getAmount())
                .expenseShareId(paymentShare.getExpenseShareId())
                .expenseId(paymentShare.getExpenseId())
                .build());
        paymentShare.setAmount(BigDecimal.ZERO);
      } else {
        shareVos.add(
            PaymentShareVo.builder()
                .amount(deductedAmount)
                .expenseShareId(paymentShare.getExpenseShareId())
                .expenseId(paymentShare.getExpenseId())
                .build());
        paymentShare.setAmount(paymentShare.getAmount().subtract(deductedAmount));
      }
      if (deductedAmount.compareTo(BigDecimal.ZERO) <= 0) {
        break;
      }
    }
    return shareVos;
  }
}
