package com.splitify.splitify.api.expense;

import com.splitify.splitify.api.expense.assembler.PaymentAssembler;
import com.splitify.splitify.api.expense.dto.PaymentDetailsDto;
import com.splitify.splitify.api.expense.dto.PaymentRequestDto;
import com.splitify.splitify.api.expense.dto.PaymentResponseDto;
import com.splitify.splitify.transaction.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api/payments/")
public class PaymentApiImpl implements PaymentApi {

  @Autowired private PaymentService service;
  @Autowired private PaymentAssembler assembler;
  /**
   * Record payment
   *
   * @param paymentRequest paymentRequest
   * @return Expense response
   */
  @Override
  public PaymentResponseDto recordPayment(PaymentRequestDto paymentRequest) {
    Integer paymentId = service.recordPayment(assembler.assemblePaymentRequestDto(paymentRequest));
    return PaymentResponseDto.builder().paymentId(paymentId).build();
  }

  /**
   * Updates the payment
   *
   * @param paymentId paymentId
   * @param paymentRequest expenseRequest
   * @return expense response
   */
  @Override
  public PaymentResponseDto updatePayment(Integer paymentId, PaymentRequestDto paymentRequest) {
    Integer payment =
        service.updatePayment(paymentId, assembler.assemblePaymentRequestDto(paymentRequest));
    return PaymentResponseDto.builder().paymentId(payment).build();
  }

  /**
   * Delete payment
   *
   * @param paymentId paymentId
   */
  @Override
  public void deletePayment(Integer paymentId) {
    service.deletePayment(paymentId);
  }

  /**
   * Get payment details.
   *
   * @param paymentId expenseId.
   * @return payment details.
   */
  @Override
  public PaymentDetailsDto getPaymentDetails(Integer paymentId) {
    return assembler.assemblePaymentRequest(service.getPaymentDetails(paymentId));
  }
}
