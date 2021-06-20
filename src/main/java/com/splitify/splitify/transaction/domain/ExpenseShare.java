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
@Table(name = "EXPENSE_SHARE")
@Builder
public class ExpenseShare {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "EXPENSEID", nullable = false)
  private Expense expense;
}
