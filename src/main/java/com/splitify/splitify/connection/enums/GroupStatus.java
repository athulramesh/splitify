package com.splitify.splitify.connection.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum GroupStatus {
  ACTIVE(0),
  DELETED(1);

  private int code;

  public int getCode() {
    return code;
  }
}
