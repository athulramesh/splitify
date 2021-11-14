package com.splitify.splitify.transaction.service;

import com.splitify.splitify.security.service.UserService;
import com.splitify.splitify.transaction.domain.PaymentEntity;
import com.splitify.splitify.transaction.enums.PaymentStatus;
import com.splitify.splitify.transaction.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/** The Expense service. */
@Service
public class PaymentService {
  @Autowired private PaymentRepository repository;
  @Autowired private UserService userService;
  @Autowired private ExpenseService expenseService;

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
      List<PaymentShareVo> shareVos =
          expenseService.settleExpense(
              paymentRequest.getGroupId(),
              paymentRequest.getPaidBy(),
              paymentRequest.getReceivedBy(),
              paymentRequest.getAmount());
      paymentEntity.addPaymentShare(shareVos);
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
      if (paymentEntity.getAmount().compareTo(paymentRequest.getAmount()) > 0) {
        List<PaymentShareVo> shareVos =
            paymentEntity.updatePaymentShare(
                paymentEntity.getAmount().subtract(paymentRequest.getAmount()));
        expenseService.updatePaymentDeduction(shareVos);
      } else if (paymentEntity.getAmount().compareTo(paymentRequest.getAmount()) < 0) {
        List<PaymentShareVo> shareVos =
            expenseService.settleExpense(
                paymentRequest.getGroupId(),
                paymentRequest.getPaidBy(),
                paymentRequest.getReceivedBy(),
                paymentRequest.getAmount().subtract(paymentEntity.getAmount()));
        paymentEntity.addPaymentShare(shareVos);
      }
      paymentEntity.updatePayment(paymentRequest);
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
      List<PaymentShareVo> shareVos = paymentEntity.updatePaymentShare(paymentEntity.getAmount());
      expenseService.updatePaymentDeduction(shareVos);
      paymentEntity.delete();
      repository.save(paymentEntity);
    }
  }

  /**
   * Get Expense details
   *
   * @param groupId groupId
   * @param userId userId
   * @param date date
   * @return expense details
   */
  public PaymentDetails getPaymentDetails(Integer groupId, Integer userId, Calendar date) {
    List<PaymentEntity> payments = repository.getPaymentOfUser(userId, groupId, date);
    List<PaymentDetailsList> paymentDetailsLists = new ArrayList<>();
    payments.forEach(
        p -> {
          paymentDetailsLists.add(buildPayment(p));
        });
    return PaymentDetails.builder().payments(paymentDetailsLists).build();
  }

  /**
   * builds the expense details
   *
   * @param paymentEntity paymentEntity
   * @return expense details
   */
  private PaymentDetailsList buildPayment(PaymentEntity paymentEntity) {
    return PaymentDetailsList.builder()
        .paymentId(paymentEntity.getPaymentId())
        .amount(paymentEntity.getAmount())
        .createdBy(userService.getUserById(paymentEntity.getCreatedBy()))
        .groupId(paymentEntity.getGroupId())
        .paidBy(userService.getUserById(paymentEntity.getFromId()))
        .receivedBy(userService.getUserById(paymentEntity.getToId()))
        .onDate(paymentEntity.getOnDate())
        .build();
  }
}
