package com.splitify.splitify.transaction.domain;

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
@Table(name = "EXPENSESHARE")
@Builder
public class ExpenseShareEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "EXPENSESHAREID")
  private Integer expenseShareId;

  @Column(name = "OWNEDBY")
  private Integer owedBy;

  @Column(name = "AMOUNT")
  private BigDecimal amount;

  @Column(name = "SETTLEDAMOUNT")
  private BigDecimal settledAmount;

  @Column(name = "PAIDDATE")
  private Calendar paidDate;

  @Column(name = "STATUS")
  private Integer status;

  @Column(name = "PAYMENTSTATUS")
  private Integer paymentStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "EXPENSEID", nullable = false)
  private ExpenseEntity expense;

  /**
   * Get the remaining amount
   *
   * @return remaining amount
   */
  public BigDecimal getRemainingAmount() {
    return getAmount().subtract(getSettledAmount());
  }
}
