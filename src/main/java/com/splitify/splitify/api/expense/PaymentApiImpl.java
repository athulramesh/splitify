package com.splitify.splitify.api.expense;

import com.splitify.splitify.api.expense.assembler.PaymentAssembler;
import com.splitify.splitify.api.expense.dto.PaymentDetailsDto;
import com.splitify.splitify.api.expense.dto.PaymentRequestDto;
import com.splitify.splitify.api.expense.dto.PaymentResponseDto;
import com.splitify.splitify.transaction.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;

@RestController
@RequestMapping("v1/api/payments/")
@CrossOrigin(origins = "https://simplifysplit.web.app/", allowedHeaders = "*")
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
   * @param groupId groupId.
   * @return payment details.
   */
  @Override
  public PaymentDetailsDto getPaymentDetails(
      @PathVariable("groupId") Integer groupId,
      @PathVariable("userId") Integer userId,
      @RequestParam Calendar date) {
    return assembler.assemblePaymentRequest(service.getPaymentDetails(groupId, userId, date));
  }
}
