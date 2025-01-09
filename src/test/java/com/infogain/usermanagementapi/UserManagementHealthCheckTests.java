package com.infogain.usermanagementapi;

import static org.apache.http.HttpStatus.SC_OK;
import static com.infogain.api.healthcheck.HealthCheckAPI.healthCheckForUserManagementApi;
import static com.infogain.enums.Author.RAVI;
import static com.infogain.enums.Category.SMOKE;
import static com.infogain.enums.Service.USER_MANAGEMENT;

import com.infogain.annotations.FrameworkAnnotations;
import com.infogain.annotations.HealthCheckTest;
import groovy.util.logging.Slf4j;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

@Slf4j
@HealthCheckTest
@FrameworkAnnotations(Author = { RAVI }, Category = SMOKE, Service = USER_MANAGEMENT)
class UserManagementHealthCheckTests {
  @Test
  void assertThatUserManagementServiceIsUpAndHealthy() {
    Response response = healthCheckForUserManagementApi();

    VerifyUserManagementHealthCheckResponse.assertThat(response, VerifyUserManagementHealthCheckResponse.class)
        .statusCodeIs(SC_OK)
        .hasKeyWithValue("status", "UP");
  }
}
