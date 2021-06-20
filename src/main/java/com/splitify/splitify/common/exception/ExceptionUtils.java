package com.splitify.splitify.common.exception;

import org.springframework.stereotype.Service;

/** Exception utils */
@Service
public class ExceptionUtils {
  /**
   * Throw bad request exception.
   *
   * @param message message.
   */
  public void throwBadRequestException(String message) {
    throw new SplitifyException(message);
  }
}
