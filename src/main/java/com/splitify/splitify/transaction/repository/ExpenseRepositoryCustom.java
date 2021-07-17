package com.splitify.splitify.transaction.repository;

import com.querydsl.core.Tuple;
import com.splitify.splitify.transaction.domain.ExpenseEntity;

import java.util.List;

public interface ExpenseRepositoryCustom {
  /**
   * Get total Due amount in group
   *
   * @param fromId fromId
   * @param groupId groupId
   * @return total due amount
   */
  List<Tuple> getTotalDueAmountInGroup(Integer fromId, Integer groupId);

  /**
   * Get total Due amount in group
   *
   * @param toId toId
   * @param groupId groupId
   * @return total due amount
   */
  List<Tuple> getTotalPayableAmountInGroup(Integer toId, Integer groupId);

  /**
   * Get total Due amount per group
   *
   * @param fromId fromId
   * @return total due amount
   */
  List<Tuple> getTotalDueAmountPerGroup(Integer fromId);
  /**
   * Get total Due amount per group
   *
   * @param toId toId
   * @return total due amount
   */
  List<Tuple> getTotalPayableAmountPerGroup(Integer toId);

  /**
   * Get expenses by owner
   *
   * @param groupId groupId
   * @param paidBy paidBy
   * @return expenses
   */
  List<ExpenseEntity> getExpensesByOwner(Integer groupId, Integer paidBy);

  /**
   * Get expenses of user
   *
   * @param userId userId
   * @return expense of user
   */
  List<ExpenseEntity> getExpensesOfUser(Integer userId, Integer groupId);
}
