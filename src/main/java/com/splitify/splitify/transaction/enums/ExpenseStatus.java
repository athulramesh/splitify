package com.splitify.splitify.transaction.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum ExpenseStatus {
  ACTIVE(0),
  CANCELLED(1);

  private int code;

  public int getCode() {
    return code;
  }
}
