package com.splitify.splitify.transaction.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.splitify.splitify.transaction.domain.PaymentEntity;
import com.splitify.splitify.transaction.domain.QPaymentEntity;
import com.splitify.splitify.transaction.service.DebtVo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.Calendar;
import java.util.List;

public class PaymentRepositoryCustomImpl implements PaymentRepositoryCustom {
  @Autowired private EntityManager entityManager;
  /**
   * Get expenses shared of user
   *
   * @param userId userId
   * @param groupId groupId
   * @param date date
   * @return expense of user
   */
  @Override
  public List<PaymentEntity> getPaymentOfUser(Integer userId, Integer groupId, Calendar date) {
    QPaymentEntity paymentEntity = QPaymentEntity.paymentEntity;
    BooleanBuilder where = new BooleanBuilder();
    where.and(paymentEntity.groupId.eq(groupId));
    where.and(paymentEntity.onDate.goe(date));
    where.and(paymentEntity.fromId.eq(userId).or(paymentEntity.toId.eq(userId)));

    JPAQuery<DebtVo> query = new JPAQuery<>(entityManager);
    return query
        .select(paymentEntity)
        .from(paymentEntity)
        .where(where)
        .orderBy(paymentEntity.onDate.asc())
        .fetch();
  }
}
