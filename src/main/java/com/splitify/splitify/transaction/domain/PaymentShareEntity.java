package com.splitify.splitify.transaction.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PAYMENTSHARE")
@Builder
public class PaymentShareEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "PAYMENTSHAREID")
  private Integer paymentShareId;

  @Column(name = "EXPENSEID")
  private Integer expenseId;

  @Column(name = "EXPENSESHAREID")
  private Integer expenseShareId;

  @Column(name = "AMOUNT")
  private BigDecimal amount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PAYMENTID", nullable = false)
  private PaymentEntity payment;
}
