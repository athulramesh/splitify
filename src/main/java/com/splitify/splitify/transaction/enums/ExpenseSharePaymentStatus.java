package com.splitify.splitify.transaction.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum ExpenseSharePaymentStatus {
  SETTLED(0),
  PARTIALLY_SETTLED(1),
  UNSETTLED(2);

  private int code;

  public int getCode() {
    return code;
  }
}
