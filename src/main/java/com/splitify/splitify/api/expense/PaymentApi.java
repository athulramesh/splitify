package com.splitify.splitify.api.expense;

import com.splitify.splitify.api.expense.dto.PaymentDetailsDto;
import com.splitify.splitify.api.expense.dto.PaymentRequestDto;
import com.splitify.splitify.api.expense.dto.PaymentResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/api/payments/")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public interface PaymentApi {

  /**
   * Record payment
   *
   * @param paymentRequest paymentRequest
   * @return Expense response
   */
  @PostMapping
  PaymentResponseDto recordPayment(@RequestBody PaymentRequestDto paymentRequest);

  /**
   * Updates the payment
   *
   * @param paymentId paymentId
   * @param paymentRequest expenseRequest
   * @return expense response
   */
  @PutMapping("{paymentId}/")
  PaymentResponseDto updatePayment(
      @PathVariable("paymentId") Integer paymentId, @RequestBody PaymentRequestDto paymentRequest);

  /**
   * Delete payment
   *
   * @param paymentId paymentId
   */
  @PutMapping("{paymentId}/delete")
  void deletePayment(@PathVariable("paymentId") Integer paymentId);

  /**
   * Get payment details.
   *
   * @param paymentId expenseId.
   * @return payment details.
   */
  @GetMapping("{paymentId}/")
  PaymentDetailsDto getPaymentDetails(@PathVariable("paymentId") Integer paymentId);
}
