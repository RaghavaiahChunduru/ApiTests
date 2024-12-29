package com.infogain.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class StringUtil {

  private StringUtil() {}

  public static void validateNonNullAndNonEmpty(String input, String fieldName) {
    if (input == null || input.isEmpty()) {
      log.error("{} cannot be null or empty.", fieldName);
      throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
    }
  }
}
