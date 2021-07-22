package com.splitify.splitify.transaction.repository;

import com.splitify.splitify.transaction.domain.PaymentEntity;

import java.util.Calendar;
import java.util.List;

public interface PaymentRepositoryCustom {

  /**
   * Get expenses shared of user
   *
   * @param userId userId
   * @return expense of user
   */
  List<PaymentEntity> getPaymentOfUser(Integer userId, Integer groupId, Calendar date);
}
