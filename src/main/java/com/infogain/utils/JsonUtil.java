package com.infogain.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.machinezoo.noexception.Exceptions;

import lombok.extern.slf4j.Slf4j;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
public class JsonUtil {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private JsonUtil() {
  }

  public static <T> String serialize(T pojo) {
    return Exceptions.wrap(e -> new RuntimeException(
        "Serialization error for POJO: " + pojo.getClass().getSimpleName(), e))
        .get(() -> OBJECT_MAPPER.writeValueAsString(pojo));
  }

  public static <T> T deserialize(String json, Class<T> clazz) {
    return Exceptions.wrap(e -> new RuntimeException(
        "Deserialization error for class: " + clazz.getSimpleName(), e))
        .get(() -> {
          log.info(json);
          return OBJECT_MAPPER.readValue(json, clazz);
        });
  }

  public static String readQueryFromJsonFile(String filePath) {
    return Exceptions.wrap(e -> new RuntimeException(
        "Error reading query from JSON file: " + filePath, e))
        .get(() -> {
          String json = new String(Files.readAllBytes(Path.of(filePath)));
          return json.trim();
        });
  }

  public static void writeFromMapToJsonFile(Map<String, Integer> map, String path) {
    ObjectMapper objectMapper = new ObjectMapper();
    Exceptions.wrap(e -> new RuntimeException("Error writing map data to JSON file", e))
        .run(() -> {
          Path filePath = Paths.get(path);
          Files.createDirectories(filePath.getParent());
          objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), map);
        });
  }

  public static Map<String, Integer> writeFromJsonToMap(String path) {
    return Exceptions.wrap(e -> new RuntimeException("Failed to load from JSON file at " + path, e))
        .get(() -> {
          Path filePath = Paths.get(path);
          ObjectMapper objectMapper = new ObjectMapper();

          if (!Files.exists(filePath)) {
            throw new RuntimeException("JSON file not found at " + filePath.toAbsolutePath());
          }

          return objectMapper.readValue(filePath.toFile(), new TypeReference<Map<String, Integer>>() {
          });
        });
  }
}
