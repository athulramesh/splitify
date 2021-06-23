package com.splitify.splitify.transaction.service;

import com.splitify.splitify.security.service.UserService;
import com.splitify.splitify.transaction.domain.PaymentEntity;
import com.splitify.splitify.transaction.enums.PaymentStatus;
import com.splitify.splitify.transaction.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** The Expense service. */
@Service
public class PaymentService {
  @Autowired private PaymentRepository repository;
  @Autowired private UserService userService;

  /**
   * Records the expense
   *
   * @param paymentRequest expenseRequest
   * @return expense id
   */
  public Integer recordPayment(PaymentRequest paymentRequest) {
    if (paymentRequest != null) {
      PaymentEntity paymentEntity =
          PaymentEntity.builder()
              .amount(paymentRequest.getAmount())
              .createdBy(paymentRequest.getCreatedBy())
              .groupId(paymentRequest.getGroupId())
              .fromId(paymentRequest.getPaidBy())
              .toId(paymentRequest.getReceivedBy())
              .onDate(paymentRequest.getOnDate())
              .status(PaymentStatus.ACTIVE.getCode())
              .build();

      return repository.save(paymentEntity).getPaymentId();
    }
    return null;
  }

  /**
   * Updates the expense
   *
   * @param paymentId paymentId
   * @param paymentRequest paymentRequest
   * @return payment id
   */
  public Integer updatePayment(Integer paymentId, PaymentRequest paymentRequest) {
    PaymentEntity paymentEntity = getPaymentEntity(paymentId);
    if (paymentEntity != null) {
      paymentEntity.updateExpense(paymentRequest);
      repository.save(paymentEntity);
      return paymentEntity.getPaymentId();
    }
    return null;
  }

  /**
   * Get payment entity
   *
   * @param paymentId paymentId
   * @return payment entity
   */
  private PaymentEntity getPaymentEntity(Integer paymentId) {
    return repository.findById(paymentId).orElse(null);
  }

  /**
   * Delete the payment
   *
   * @param paymentId expenseId
   */
  public void deletePayment(Integer paymentId) {
    PaymentEntity paymentEntity = getPaymentEntity(paymentId);
    if (paymentEntity != null) {
      paymentEntity.delete();
      repository.save(paymentEntity);
    }
  }

  /**
   * Get Expense details
   *
   * @param paymentId expenseId
   * @return expense details
   */
  public PaymentDetails getPaymentDetails(Integer paymentId) {
    PaymentEntity paymentEntity = getPaymentEntity(paymentId);
    if (paymentEntity != null) {
      return buildPayment(paymentEntity);
    }
    return null;
  }

  /**
   * builds the expense details
   *
   * @param paymentEntity paymentEntity
   * @return expense details
   */
  private PaymentDetails buildPayment(PaymentEntity paymentEntity) {
    return PaymentDetails.builder()
        .amount(paymentEntity.getAmount())
        .createdBy(userService.getUserById(paymentEntity.getCreatedBy()))
        .groupId(paymentEntity.getGroupId())
        .paidBy(userService.getUserById(paymentEntity.getFromId()))
        .receivedBy(userService.getUserById(paymentEntity.getToId()))
        .onDate(paymentEntity.getOnDate())
        .build();
  }
}
