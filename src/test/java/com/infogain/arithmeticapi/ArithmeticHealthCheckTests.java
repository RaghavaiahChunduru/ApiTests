package com.infogain.arithmeticapi;

import static org.apache.http.HttpStatus.SC_OK;
import static com.infogain.api.healthcheck.HealthCheckAPI.healthCheckForArithmeticApi;
import static com.infogain.enums.Author.RAVI;
import static com.infogain.enums.Category.SMOKE;
import static com.infogain.enums.Service.ARITHMETIC_OPERATION;

import com.infogain.annotations.FrameworkAnnotations;
import com.infogain.annotations.HealthCheckTest;
import com.infogain.report.ExtentLogger;
import groovy.util.logging.Slf4j;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

@Slf4j
@HealthCheckTest
@FrameworkAnnotations(Author = { RAVI }, Category = SMOKE, Service = ARITHMETIC_OPERATION)
class ArithmeticHealthCheckTests {
  @Test
  void assertThatArithmeticServiceIsUpAndHealthy() {
    Response response = healthCheckForArithmeticApi();
    ExtentLogger.logResponse(response);

    VerifyArithmeticHealthCheckResponse.assertThat(response, VerifyArithmeticHealthCheckResponse.class)
        .statusCodeIs(SC_OK)
        .hasKeyWithValue("status", "UP");
  }
}
