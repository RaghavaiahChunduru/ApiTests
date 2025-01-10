package com.infogain.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

@Slf4j
public class JsonUtil {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private JsonUtil() {
  }

  // Serialize POJO to JSON string
  public static <T> String serialize(T pojo) throws RuntimeException {
    try {
      return OBJECT_MAPPER.writeValueAsString(pojo);
    } catch (Exception e) {
      throw new RuntimeException(
          "Serialization error for POJO: " + pojo.getClass().getSimpleName(), e);
    }
  }

  // Deserialize JSON string to POJO
  public static <T> T deserialize(String json, Class<T> clazz) throws RuntimeException {
    try {
      log.info(json);
      return OBJECT_MAPPER.readValue(json, clazz);
    } catch (Exception e) {
      throw new RuntimeException("Deserialization error for class: " + clazz.getSimpleName(), e);
    }
  }

  // Read query from JSON file directly as string
  public static String readQueryFromJsonFile(String filePath) throws RuntimeException {
    try {
      String json = new String(Files.readAllBytes(Path.of(filePath)));
      return json.trim();
    } catch (IOException e) {
      throw new RuntimeException("Error reading query from JSON file: " + filePath, e);
    }
  }
}
