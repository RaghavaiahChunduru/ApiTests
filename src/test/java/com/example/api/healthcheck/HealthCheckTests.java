package com.example.api.healthcheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import com.example.annotations.HealthCheckTest;
import com.example.report.ExtentLogger;
import groovy.util.logging.Slf4j;

@Slf4j
@HealthCheckTest
class HealthCheckTests {
  @Test
  void assertThatRestfulBookerApplicationIsUpAndHealthy() {
    Response response = HealthCheckAPI.healthCheck();
    ExtentLogger.logResponse(response);
    assertEquals(201, response.getStatusCode());
  }
}
