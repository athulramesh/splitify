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
  private Integer expenseShareId;

  private Integer owedBy;
  private BigDecimal amount;
  private BigDecimal settledAmount;
  private Calendar paidDate;
  private String status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "EXPENSEID", nullable = false)
  private Expense expense;
}
