package com.infogain.utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.machinezoo.noexception.Exceptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class StringUtil {

  private StringUtil() {
  }

  public static void validateNonNullAndNonEmpty(String input, String fieldName) {
    if (input == null || input.isEmpty()) {
      log.error("{} cannot be null or empty.", fieldName);
      throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
    }
  }

  public static String loadQueryFromFile(String filePath) {
    return Exceptions.wrap(e -> new RuntimeException("Error loading query from file: " + filePath, e))
        .get(() -> new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8));
  }
}