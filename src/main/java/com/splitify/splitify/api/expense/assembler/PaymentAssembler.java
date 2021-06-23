package com.splitify.splitify.api.expense.assembler;

import com.splitify.splitify.api.expense.dto.PaymentDetailsDto;
import com.splitify.splitify.api.expense.dto.PaymentRequestDto;
import com.splitify.splitify.transaction.service.PaymentDetails;
import com.splitify.splitify.transaction.service.PaymentRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentAssembler {
  @Autowired private ModelMapper modelMapper;

  public PaymentRequest assemblePaymentRequestDto(PaymentRequestDto paymentRequest) {
    return modelMapper.map(paymentRequest, PaymentRequest.class);
  }

  public PaymentDetailsDto assemblePaymentRequest(PaymentDetails paymentDetails) {
    return modelMapper.map(paymentDetails, PaymentDetailsDto.class);
  }
}
