package com.infogain.usermanagementapi;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.infogain.api.healthcheck.HealthCheckAPI.healthCheckForUserManagementApi;

import com.infogain.report.ExtentLogger;
import com.infogain.annotations.HealthCheckTest;
import groovy.util.logging.Slf4j;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

@Slf4j
@HealthCheckTest
class UserManagementHealthCheckTests {
  @Test
  void assertThatUserManagementServiceIsUpAndHealthy() {
    Response response = healthCheckForUserManagementApi();
    ExtentLogger.logResponse(response);
    assertEquals(SC_OK, response.getStatusCode());
  }
}
