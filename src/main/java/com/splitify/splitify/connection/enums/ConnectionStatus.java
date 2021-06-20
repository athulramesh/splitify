package com.splitify.splitify.connection.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum ConnectionStatus {
  NEW(0),
  ACTIVE(1),
  REJECTED(2);

  private int code;

  public int getCode() {
    return code;
  }
}
