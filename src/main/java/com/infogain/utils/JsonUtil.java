package com.infogain.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private JsonUtil() {}

  public static <T> String serialize(T pojo) throws RuntimeException {
    try {
      return OBJECT_MAPPER.writeValueAsString(pojo);
    } catch (Exception e) {
      throw new RuntimeException(
          "Serialization error for POJO: " + pojo.getClass().getSimpleName(), e);
    }
  }

  public static <T> T deserialize(String json, Class<T> clazz) throws RuntimeException {
    try {
      return OBJECT_MAPPER.readValue(json, clazz);
    } catch (Exception e) {
      throw new RuntimeException("Deserialization error for class: " + clazz.getSimpleName(), e);
    }
  }
}
