package com.splitify.splitify.transaction.domain;

import com.splitify.splitify.transaction.enums.PaymentStatus;
import com.splitify.splitify.transaction.service.PaymentRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Calendar;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PAYMENT")
@Builder
public class PaymentEntity {

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
  public void updateExpense(PaymentRequest paymentRequest) {
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
}
