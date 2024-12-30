package com.infogain.usermanagementapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.infogain.annotations.HealthCheckTest;
import com.infogain.api.healthcheck.HealthCheckAPI;
import com.infogain.report.ExtentLogger;
import groovy.util.logging.Slf4j;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

@Slf4j
@HealthCheckTest
class UserManagementHealthCheckTests {
  @Test
  void assertThatUserManagementServiceIsUpAndHealthy() {
    Response response = HealthCheckAPI.healthCheck();
    ExtentLogger.logResponse(response);
    assertEquals(200, response.getStatusCode());
  }
}
