package com.splitify.splitify.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Splitify exception schema */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SplitifyExceptionSchema {
  /** The exception message. */
  private String message;
}
