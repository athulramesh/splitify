package com.splitify.splitify.connection.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum GroupMemberStatus {
  ACTIVE(0),
  REMOVED(1);

  private int code;

  public int getCode() {
    return code;
  }
}
