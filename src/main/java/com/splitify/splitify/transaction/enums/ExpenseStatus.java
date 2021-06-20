package com.splitify.splitify.transaction.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum ExpenseStatus {
  SETTLED(0),
  PARTIALLY_SETTLED(1),
  FULLY_SETTLED(2);

  private int code;

  public int getCode() {
    return code;
  }
}
