package com.infogain.usermanagementapi;

import static org.apache.http.HttpStatus.SC_OK;
import static com.infogain.api.healthcheck.HealthCheckAPI.healthCheckForUserManagementApi;

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

    VerifyUserManagementHealthCheckResponse.assertThat(response, VerifyUserManagementHealthCheckResponse.class)
        .statusCodeIs(SC_OK)
        .hasKeyWithValue("status", "UP");
  }
}
