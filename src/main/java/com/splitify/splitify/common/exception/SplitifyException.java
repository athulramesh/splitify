package com.splitify.splitify.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** Splitify exception class. */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class SplitifyException extends RuntimeException {
  /** The exception message. */
  private String message;
}
