package com.splitify.splitify.transaction.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.splitify.splitify.transaction.domain.QExpenseEntity;
import com.splitify.splitify.transaction.domain.QExpenseShareEntity;
import com.splitify.splitify.transaction.service.DueVo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

public class ExpenseRepositoryCustomImpl implements ExpenseRepositoryCustom {

  @Autowired private EntityManager entityManager;

  /**
   * Get total Due amount in group
   *
   * @param fromId fromId
   * @param groupId groupId
   * @return total due amount
   */
  public List<Tuple> getTotalDueAmountInGroup(Integer fromId, Integer groupId) {
    QExpenseEntity expenseEntity = QExpenseEntity.expenseEntity;
    QExpenseShareEntity expenseShareEntity = QExpenseShareEntity.expenseShareEntity;
    BooleanBuilder where = new BooleanBuilder();
    where.and(expenseEntity.paidBy.eq(fromId));
    where.and(expenseShareEntity.paymentStatus.in(1, 2));
    where.and(expenseEntity.groupId.eq(groupId));

    JPAQuery<DueVo> query = new JPAQuery<>(entityManager);
    return query
        .select(
            expenseShareEntity.owedBy,
            expenseShareEntity.amount.sum().subtract(expenseShareEntity.settledAmount.sum()))
        .from(expenseEntity)
        .innerJoin(expenseShareEntity)
        .on(expenseEntity.expenseId.eq(expenseShareEntity.expense.expenseId))
        .groupBy(expenseShareEntity.owedBy)
        .where(where)
        .fetch();
  }

  /**
   * Get total Due amount in group
   *
   * @param toId toId
   * @param groupId groupId
   * @return total due amount
   */
  @Override
  public List<Tuple> getTotalPayableAmountInGroup(Integer toId, Integer groupId) {
    QExpenseEntity expenseEntity = QExpenseEntity.expenseEntity;
    QExpenseShareEntity expenseShareEntity = QExpenseShareEntity.expenseShareEntity;
    BooleanBuilder where = new BooleanBuilder();
    where.and(expenseShareEntity.owedBy.eq(toId));
    where.and(expenseShareEntity.paymentStatus.in(1, 2));
    where.and(expenseEntity.groupId.eq(groupId));

    JPAQuery<DueVo> query = new JPAQuery<>(entityManager);
    return query
        .select(
            expenseEntity.paidBy,
            expenseShareEntity.amount.sum().subtract(expenseShareEntity.settledAmount.sum()))
        .from(expenseEntity)
        .innerJoin(expenseShareEntity)
        .on(expenseEntity.expenseId.eq(expenseShareEntity.expense.expenseId))
        .groupBy(expenseEntity.paidBy)
        .where(where)
        .fetch();
  }

  /**
   * Get total Due amount per group
   *
   * @param fromId fromId
   * @return total due amount
   */
  @Override
  public List<Tuple> getTotalDueAmountPerGroup(Integer fromId) {
    QExpenseEntity expenseEntity = QExpenseEntity.expenseEntity;
    QExpenseShareEntity expenseShareEntity = QExpenseShareEntity.expenseShareEntity;
    BooleanBuilder where = new BooleanBuilder();
    where.and(expenseEntity.paidBy.eq(fromId));
    where.and(expenseShareEntity.paymentStatus.in(1, 2));

    JPAQuery<DueVo> query = new JPAQuery<>(entityManager);
    return query
        .select(
            expenseEntity.groupId,
            expenseShareEntity.owedBy,
            expenseShareEntity.amount.sum().subtract(expenseShareEntity.settledAmount.sum()))
        .from(expenseEntity)
        .innerJoin(expenseShareEntity)
        .on(expenseEntity.expenseId.eq(expenseShareEntity.expense.expenseId))
        .groupBy(expenseEntity.groupId)
        .groupBy(expenseShareEntity.owedBy)
        .where(where)
        .fetch();
  }

  /**
   * Get total Due amount per group
   *
   * @param toId toId
   * @return total due amount
   */
  @Override
  public List<Tuple> getTotalPayableAmountPerGroup(Integer toId) {
    QExpenseEntity expenseEntity = QExpenseEntity.expenseEntity;
    QExpenseShareEntity expenseShareEntity = QExpenseShareEntity.expenseShareEntity;
    BooleanBuilder where = new BooleanBuilder();
    where.and(expenseShareEntity.owedBy.eq(toId));
    where.and(expenseShareEntity.paymentStatus.in(1, 2));

    JPAQuery<DueVo> query = new JPAQuery<>(entityManager);
    return query
        .select(
            expenseEntity.groupId,
            expenseEntity.paidBy,
            expenseShareEntity.amount.sum().subtract(expenseShareEntity.settledAmount.sum()))
        .from(expenseEntity)
        .innerJoin(expenseShareEntity)
        .on(expenseEntity.expenseId.eq(expenseShareEntity.expense.expenseId))
        .groupBy(expenseEntity.groupId)
        .groupBy(expenseEntity.paidBy)
        .where(where)
        .fetch();
  }
}
