package com.splitify.splitify.transaction.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EXPENSE")
@Builder
public class ExpenseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "EXPENSEID")
  private Integer expenseId;

  @Column(name = "EXPENSENAME")
  private String expenseName;

  @Column(name = "GROUPID")
  private Integer groupId;

  @Column(name = "AMOUNT")
  private BigDecimal amount;

  @Column(name = "STATUS")
  private Integer status;

  @Column(name = "ONDATE")
  private Calendar onDate;

  @Column(name = "PAIDBY")
  private Integer paidBy;

  @Column(name = "CREATEDBY")
  private Integer createdBy;

  @OneToMany(
      mappedBy = "expense",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<ExpenseShareEntity> expenseShare;
}
